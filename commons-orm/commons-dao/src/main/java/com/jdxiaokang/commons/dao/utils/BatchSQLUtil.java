package com.jdxiaokang.commons.dao.utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import static com.jdxiaokang.commons.dao.commons.SQLConstant.*;


/**
 * @author Administrator
 * @Description 测试批量
 * @create 2019-01-09 10:33
 */
public class BatchSQLUtil {

    private static final char UNDERLINE = '_';
    private static final ConcurrentMap<String, MetaData> FIELDS = Maps.newConcurrentMap();

    public static String batchInsert(Map<String, List<?>> map) throws IllegalAccessException {
        List<?> list = map.get("list");
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalAccessException("empty params");
        }
        Object model = list.get(0);
        Class<?> clazz = model.getClass();
        return batchInsertSQL(clazz, model, list);
    }

    private static String batchInsertSQL(Class<?> clazz, Object model, List<?> list) throws IllegalAccessException {
        MetaData metaData = getMetaData(clazz);
        StringBuilder base = new StringBuilder("insert into").append("\n").append(metaData.tableNameValue).append("\n(");
        StringBuilder pattern = new StringBuilder("(");
        for (Field f : metaData.getFieldList()) {
            Object o = f.get(model);
            if (null != o) {
                base.append(camelToUnderline(f.getName()));
                pattern.append("#'{'list[{0}].").append(f.getName()).append("}");
                base.append(",");
                pattern.append(",");
            }
        }
        //需要加默认填充字段
        base.append(CREATE_BY_SQL).append(',').append(UPDATE_BY_SQL).append(')');
        pattern.append("#'{'list[{0}].").append(CREATE_BY).append("},#'{'list[{0}].").append(UPDATE_BY).append("})");
        base.append("\n").append("VALUES").append("\n");
        MessageFormat mf = new MessageFormat(pattern.toString());
        for (int i = 0; i < list.size(); i++) {
            base.append(mf.format(new Object[]{i + ""}));
            if (i != list.size() - 1) {
                base.append(",");
            }
        }
        return base.toString();
    }

    private static MetaData getMetaData(Class<?> clazz) {
        String className = clazz.getName();
        return Optional.ofNullable(FIELDS.get(className)).orElseGet(() ->
                FIELDS.computeIfAbsent(clazz.getName(), name -> {
                    TableName tableName = clazz.getAnnotation(TableName.class);
                    if (null == tableName) {
                        throw new RuntimeException("Missing annotation TableName");
                    }
                    String tableNameValue = tableName.value();
                    if (StringUtils.isBlank(tableNameValue)) {
                        throw new RuntimeException("empty annotation TableName");
                    }
                    Field[] fields = clazz.getDeclaredFields();
                    List<Field> list = Lists.newArrayListWithCapacity(fields.length);
                    for (Field f : fields) {
                        if (Modifier.isStatic(f.getModifiers())) {
                            continue;
                        }
                        f.setAccessible(true);
                        TableField tableField = f.getAnnotation(TableField.class);
                        if (null != tableField) {
                            //不是数据库字段
                            if (!tableField.exist()) {
                                continue;
                            }
                            //自动填充字段，最下面已经补了
                            if (tableField.fill() != FieldFill.DEFAULT) {
                                continue;
                            }
                        }
                        list.add(f);
                    }
                    return new MetaData().setTableNameValue(tableNameValue).setFieldList(list);
                }));
    }


    private static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Data
    @Accessors(chain = true)
    private static final class MetaData {
        private String tableNameValue;
        private List<Field> fieldList;
    }


}
