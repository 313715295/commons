package com.jdxiaokang.commons.core.register;

import com.jdxiaokang.commons.core.config.PropertiesConfig;
import com.jdxiaokang.commons.core.utils.BIdGenerator;
import com.jdxiaokang.commons.core.utils.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 配置机器id
 * @date 2020/1/3
 */
@Component
@Slf4j
public class MachineRegister {

    private final RedisTemplate<String, Object> redisTemplate;


    public MachineRegister(RedisTemplate<String, Object> redisTemplate, PropertiesConfig propertiesConfig) {
        this.redisTemplate = redisTemplate;
        this.machineIdKey ="machine:register:" + propertiesConfig.getServiceName() + ":";
    }
    /**
     * 机器id
     */
    public Integer machineId = 10;
    public Integer count = 1;
    /**
     * 最大机器数64
     */
    public int max = 64;

    /**
     * 机器码注册的key
     */
    private String machineIdKey;
    /**
     * 本地ip地址
     */
    private String localIp;
    private TimeUnit timeUnit = TimeUnit.DAYS;
    /**
     * 是否已经开启定时线程
     */
    private boolean hasTimer = false;


    /**
     * 获取ip地址
     *
     * @throws UnknownHostException
     */
    private String getIPAddress() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostAddress();
    }

    /**
     * hash机器IP初始化一个机器ID
     */
    @PostConstruct
    public void initMachineId() throws Exception {
        localIp = getIPAddress();
        long ip_ = Long.parseLong(localIp.replaceAll("\\.", ""));
        //这里取64,为后续机器Ip调整做准备。
        machineId = Long.hashCode(ip_) & (max - 1);
        //创建一个机器ID
        createMachineId();
        log.info("初始化 machine_id :{}", machineId);
        BIdGenerator.initMachineId(machineId);
        SnowFlakeUtil.initWorkId(machineId);
    }

    /**
     * 容器销毁前清除注册记录
     */
    @PreDestroy
    public void destroyMachineId() {
        redisTemplate.delete(machineIdKey + machineId);
    }


    /**
     * 主方法：获取一个机器id
     *
     * @return
     */
    public Integer createMachineId() {
        try {
            log.info("尝试注册机器码=[{}],当前count=[{}]", machineId, count);
            //向redis注册，并设置超时时间
            Boolean aBoolean = registerMachine(machineId);
            //注册失败，如果是首次则判断是否是之前没成功注销掉
            if (!aBoolean) {
                if (count == 1) {
                    Boolean b = checkIsLocalIp(String.valueOf(machineId));
                    if (b) {
                        aBoolean = true;
                    }
                }
            }
            //注册成功
            if (aBoolean) {
                //启动一个线程更新超时时间
                updateExpTimeThread();
                //返回机器Id
                return machineId;
            }
            //检查是否被注册满了.不能注册，就直接返回
            if (!checkIfCanRegister()) {
                //注册满了，加一个报警
                log.warn("64个机器码已注册满");
                return machineId;
            }
            machineId += 1;
            count += 1;
            //出问题的时候出口，不会无限递归
            if (machineId >= max) {
                return machineId;
            }
            //递归调用
            createMachineId();
            //有异常 不处理，用同一个id
        } catch (Exception e) {
            log.error("", e);
        }
        return machineId;
    }

    /**
     * 检查是否被注册满了
     */
    @Nonnull
    private Boolean checkIfCanRegister() {
        //判断64个号码区间段的机器IP是否被占满
        Set<String> keys = redisTemplate.keys(machineIdKey + "[0-9]{0,2}");
        if (keys == null) {
            return true;
        }
        return keys.size() < max;
    }

    /**
     * 1.更新超時時間
     * 注意，更新前检查是否存在机器ip占用情况
     */
    private void updateExpTimeThread() {
        if (!hasTimer) {
            hasTimer = true;
            Timer timer = new Timer(localIp);
            //开启一个线程执行定时任务:
            //1.每23小时更新一次超时时间
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LocalDateTime now = LocalDateTime.now();
                    //检查缓存中的ip与本机ip是否一致，一致則更新時間，不一致則重新取一個机器ID
                    Boolean b = checkIsLocalIp(String.valueOf(machineId));
                    if (b) {
                        log.info("更新超时时间 ip:{},machineId:{}, time:{}", localIp, machineId, now);
                        redisTemplate.expire(machineIdKey + machineId, 1, timeUnit);
                    } else {
                        log.info("重新生成机器ID ip:{},machineId:{}, time:{}", localIp, machineId, now);
                        //重新生成机器ID，并且更改雪花中的机器ID
                        machineId += 1;
                        //重新生成并注册机器id
                        createMachineId();
                        //更改雪花中的机器ID
                        BIdGenerator.initMachineId(machineId);
                    }
                }
            }, 10 * 1000, 1000 * 60 * 60 * 23);
        }
    }


    /**
     * @param machineId 机器id
     */
    private Boolean checkIsLocalIp(String machineId) {
        try {
            String ip = (String) redisTemplate.opsForValue().get(machineIdKey + machineId);
            log.info("checkIsLocalIp->ip:{}", ip);
            return localIp.equals(ip);
        } catch (Exception e) {
            return Boolean.FALSE;
        }

    }

    /**
     * 1.注册机器
     * 2.设置超时时间
     *
     * @param machineId 取值为0~63
     */
    private Boolean registerMachine(Integer machineId) throws Exception {
        return redisTemplate.opsForValue().setIfAbsent(machineIdKey + machineId, localIp, 1, timeUnit);
    }

}
