package com.jdxiaokang.orm.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * created by zwq on 2018/9/2 数据源信息类，用于自动生成代码工具使用
 */
@Data
@Accessors(chain = true)
class ModuleGeneratorTemplate {
    /**
     * 模块名
     */
    private String moduleName;
    //datasource 属性
    private String userName;
    private String password;
    private String url;
    private String driverName;
    private DbType dbType;
    /**
     * 需要生成的表名
     */
    private String[] tables;

}
