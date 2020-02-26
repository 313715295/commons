package com.jdxiaokang.orm.dao.plugin.interceptors;

import com.jdxiaokang.orm.dao.plugin.page.entity.Page;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 *
 *
 * @类名称：PagingInterceptor
 * @类描述：Mybatis - 通用分页拦截器
 * @创建人：robin @修改人：
 * @修改时间：2014年9月4日 下午11:51:49 @修改备注：
 * @version 1.0.0
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class PagingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(PagingInterceptor.class);
    ;

//    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    /***
     * 批量插入模式匹配
     */
    private String regex = "^.*\\.batchInsert$";

    protected Page seekPage(Object parameter) {
        Page page = null;
        if (parameter == null) {
            return null;
        }
        if (parameter instanceof Page) {
            page = (Page) parameter;
        } else if (parameter instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) parameter;
            for (Object arg : map.values()) {
                if (arg instanceof Page) {
                    page = (Page) arg;
                }
            }
        }
        return page;
    }

    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter = null;
        Page page = null;
        MapperMethod mapperMethod = null;
        MappedStatement mappedStatement = null;

        if (invocation.getTarget() instanceof ResultSetHandler) {
            Object result = invocation.proceed();
            return result;
        } else if (invocation.getTarget() instanceof Executor) {
            mappedStatement = (MappedStatement) invocation.getArgs()[0];
            parameter = invocation.getArgs()[1];

            //查询分页拦截
            if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
                page = seekPage(parameter);
                if (page != null) {
                    Executor exe = (Executor) invocation.getTarget();

                    Connection connection = getConnection(exe.getTransaction(), mappedStatement.getStatementLog());
                    BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                    String sql = boundSql.getSql();

                    setPageParameter(sql, connection, mappedStatement, boundSql, page);

                    // 重写sql
                    String pageSql = buildPageSql(sql, page);

                    BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,
                            boundSql.getParameterMappings(), boundSql.getParameterObject());
                    MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
                    invocation.getArgs()[0] = newMs;
                    //也可以用下面的方法，暂时不用
                    invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
                }
            } else if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(mappedStatement.getId());
                if (matcher.find()) {

                }
            }

        } else if (invocation.getTarget() instanceof StatementHandler) {
        }
        // 将执行权交给下一个拦截器
        return invocation.proceed();
    }

    /**
     * 只拦截这两种类型的 <br>
     * StatementHandler <br>
     * ResultSetHandler
     *
     * @param target
     * @return
     */
    public Object plugin(Object target) {
        if (target instanceof Executor || target instanceof StatementHandler || target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    public void setProperties(Properties properties) {

    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
                ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        // setStatementTimeout()
        builder.timeout(ms.getTimeout());
        // setParameterMap()
        builder.parameterMap(ms.getParameterMap());
        // setStatementResultMap()
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        String id = "-inline";
        if (ms.getResultMaps() != null) {
            id = ms.getResultMaps().get(0).getId() + "-inline";
        }
        ResultMap resultMap = new ResultMap.Builder(null, id, ms.getResultMaps().get(0).getType(), ms.getResultMaps().get(0).getIdResultMappings()).build();
        resultMaps.add(resultMap);

        builder.resultMaps(resultMaps);
        builder.resultSetType(ms.getResultSetType());
        // setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    /**
     * 修改原SQL为分页SQL
     *
     * @param sql
     * @param page
     * @return
     */
    private String buildPageSql(String sql, Page page) {
        String pageSql = null;
        pageSql = sql + String.format(" LIMIT %d ,  %d", page.getStartIndex(), page.getPageSize());
        return pageSql.toString();
    }

    /**
     * 获取数据库连接
     * <p>
     *
     * @param transaction
     * @param statementLog
     * @return
     * @throws SQLException
     */
    protected Connection getConnection(Transaction transaction, Log statementLog) throws SQLException {
        Connection connection = transaction.getConnection();
        if (statementLog.isDebugEnabled()) {
            return ConnectionLogger.newInstance(connection, statementLog, 0);
        } else {
            return connection;
        }
    }

    /**
     * 获取总记录数
     *
     * @param sql
     * @param connection
     * @param mappedStatement
     * @param boundSql
     * @param page
     * @throws SQLException
     */
    private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql,
                                  Page page) throws SQLException {
        // 记录总记录数
        String countSql = "select count(1) from (" + sql + ") t ";
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), boundSql.getParameterObject());
            setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
            rs = countStmt.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            page.setTotalCount(totalCount);
            int totalPage = totalCount / page.getPageSize() + ((totalCount % page.getPageSize() == 0) ? 0 : 1);
            page.setToalPage(totalPage);
        } catch (SQLException e) {
            logger.error("Ignore this exception", e);
            throw e;
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("Ignore this exception", e);
            }
            try {
                countStmt.close();
            } catch (SQLException e) {
                logger.error("Ignore this exception", e);
            }
        }
    }

    /**
     * 代入参数值
     *
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}