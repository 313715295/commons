
package com.jdxiaokang.redis.redislock.registry;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 参考spring的RedisLock和redissonlock
 */
@Slf4j
public final class RedisLockRegistry {


    private final Map<String, RedisLock> locks = new ConcurrentHashMap<>();

    private final String registryKey;

    private final RedissonClient redissonClient;



    /**
     * 创建锁中心，涉及的一些线程池/锁时间等在redissonConfig中配置
     *
     * @param registryKey 锁前缀
     */
    public RedisLockRegistry(RedissonClient redissonClient, String registryKey) {
        Assert.notNull(redissonClient, "'redissonClient' cannot be null");
        Assert.notNull(registryKey, "'registryKey' cannot be null");
        this.redissonClient = redissonClient;
        this.registryKey = registryKey;
    }

    /**
     * 获取 锁
     * @param lockKey 加锁的key
     */
    public Lock obtain(String lockKey) {
        return this.locks.computeIfAbsent(lockKey, RedisLock::new);
    }

	public void expireUnusedOlderThan(long age) {
		Iterator<Map.Entry<String, RedisLock>> iterator = this.locks.entrySet().iterator();
		long now = System.currentTimeMillis();
		while (iterator.hasNext()) {
			Map.Entry<String, RedisLock> entry = iterator.next();
			RedisLockRegistry.RedisLock lock = entry.getValue();
			if (now - lock.getLockedAt() > age && !lock.isAcquiredInThisProcess()) {
				iterator.remove();
			}
		}
	}

    private final class RedisLock implements Lock {

        private final RLock rLock;

		private final String lockKey;
        private final ReentrantLock localLock = new ReentrantLock();

        private volatile long lockedAt;

        private RedisLock(String lockKey) {
			this.lockKey =lockKey;
			this.rLock = RedisLockRegistry.this.redissonClient.getLock(constructLockKey(lockKey));
        }

        public long getLockedAt() {
            return this.lockedAt;
        }

		private String constructLockKey(String path) {
			return  RedisLockRegistry.this.registryKey + ":" + path;
		}
        @Override
        public void lock() {
			try {
				lockInterruptibly();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
        }

        private void rethrowAsLockException(Exception e) {
            throw new CannotAcquireLockException("Failed to lock mutex at " + this.lockKey, e);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            this.localLock.lockInterruptibly();
            //本地锁重入，redis锁自动续期
            if (localLock.getHoldCount() > 1) {
                return;
            }
            try {
                while (!obtainLock()) {
                    Thread.sleep(100); //NOSONAR
                }
            } catch (InterruptedException ie) {
                this.localLock.unlock();
                Thread.currentThread().interrupt();
                throw ie;
            } catch (Exception e) {
                this.localLock.unlock();
                rethrowAsLockException(e);
            }
        }

        @Override
        public boolean tryLock() {
            try {
                return tryLock(0, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            long now = System.currentTimeMillis();
            if (!this.localLock.tryLock(time, unit)) {
                return false;
            }
            //本地锁重入，redis锁自动续期
            if (localLock.getHoldCount() > 1) {
                return true;
            }
            try {
                long expire = now + TimeUnit.MILLISECONDS.convert(time, unit);
                boolean acquired;
                while (!(acquired = obtainLock()) && System.currentTimeMillis() < expire) { //NOSONAR
                    Thread.sleep(100); //NOSONAR
                }
                if (!acquired) {
                    this.localLock.unlock();
                }
                return acquired;
            } catch (Exception e) {
                this.localLock.unlock();
                rethrowAsLockException(e);
            }
            return false;
        }

        private boolean obtainLock() {
            boolean result = rLock.tryLock();
            if (result) {
                this.lockedAt = System.currentTimeMillis();
            }
            return result;
        }

        @Override
        public void unlock() {
            if (!this.localLock.isHeldByCurrentThread()) {
                throw new IllegalStateException("You do not own lock at " + this.lockKey);
            }
            if (this.localLock.getHoldCount() > 1) {
                this.localLock.unlock();
                return;
            }
            try {

				rLock.unlock();
                if (log.isDebugEnabled()) {
                    log.debug("Released lock;" + this);
                }
            } catch (Exception e) {
                ReflectionUtils.rethrowRuntimeException(e);
            } finally {
                this.localLock.unlock();
            }
        }


        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Conditions are not supported");
        }

		public boolean isAcquiredInThisProcess() {
			return rLock.isLocked();
		}

		@Override
        public String toString() {
			return "RedisLock [lockKey=" + this.lockKey
					+ ",lockedAt=" + LocalDateTime.ofInstant(Instant.ofEpochMilli(lockedAt), ZoneId.systemDefault())
					+ "]";
        }

    }

}
