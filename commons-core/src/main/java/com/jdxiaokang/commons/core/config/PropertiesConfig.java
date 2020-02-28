package com.jdxiaokang.commons.core.config;

import com.jdxiaokang.commons.core.constant.PlatformConstant;
import com.jdxiaokang.commons.core.pool.ThreadConstant;
import com.jdxiaokang.commons.dao.commons.SQLConstant;
import com.jdxiaokang.commons.dao.utils.BatchSQLUtil;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 加载配置文件部分配置
 * @date 2019/12/28
 */
@Component
@Data
public class PropertiesConfig implements InitializingBean {

    /**
     * web线程池最大线程数
     */
    @Value("${server.tomcat.max-threads:200}")
    private int webMaxThreads;

    @Value("${jdxiaokang.system.company.id:1}")
    private long systemCompanyId;

    @Value("${service.name:default_service}")
    private String serviceName;

    @Value("${sql.field:createBy:create_by}")
    private String createBy;
    @Value("${sql.field:updateBy:modified_by}")
    private String updateBy;



    @Override
    public void afterPropertiesSet() {
        ThreadConstant.WEB_MAX_THREAD = webMaxThreads;

        PlatformConstant.SYSTEM_COMPANY_ID = systemCompanyId;

        SQLConstant.CREATE_BY = createBy;
        SQLConstant.CREATE_BY_FIELD = BatchSQLUtil.underLineToCamel(createBy);
        SQLConstant.UPDATE_BY = updateBy;
        SQLConstant.UPDATE_BY_FIELD = BatchSQLUtil.underLineToCamel(updateBy);
    }
}
