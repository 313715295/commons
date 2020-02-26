package com.jdxiaokang.core.validated;

import com.jdxiaokang.commons.exceptions.BaseErrorCodeEnum;
import com.jdxiaokang.commons.exceptions.JKangException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CheckConditionUtils {
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
//            log.error(e.getMessage(),e);
            return null;
        }
    }

    public static void check(Object obj){
        if(null == obj){
            throw new JKangException(BaseErrorCodeEnum.PARAM_EMPTY);
        }else if(obj instanceof Collection<?>){
            Collection objectList = (Collection)obj;
            if(CollectionUtils.isEmpty(objectList)){
                throw new JKangException(BaseErrorCodeEnum.PARAM_EMPTY);
            }
            objectList.forEach(object ->{
                check(object);
            });
        }

        List<Field> list = Arrays.asList(obj.getClass().getDeclaredFields());
        for(int i=0;i<list.size();i++){
            Field field = list.get(i);
            if(field.isAnnotationPresent(CheckCondition.class)){
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if(annotation.annotationType().equals(CheckCondition.class) ){
                        CheckCondition checkCondition = (CheckCondition)annotation;
                        if(checkCondition.checkType() == CheckTypeEnum.IS_NOT_EMPTY){
                            if(field.getGenericType() instanceof ParameterizedType){
                                Type type = ((ParameterizedType)field.getGenericType()).getRawType();
                                if(type == List.class){
                                    List objectList = (List)getFieldValueByName(field.getName(),obj);

                                    Iterator iterator = objectList.iterator();
                                    while (iterator.hasNext()) {
                                        if (iterator.next() == null) {
                                            iterator.remove();
                                        }
                                    }

                                    if(CollectionUtils.isEmpty(objectList)){
                                        throwAnnotation(field,checkCondition);
                                    }
                                    objectList.forEach(object ->{
                                        check(object);
                                    });
                                }
                            }else if(field.getGenericType().toString().equals("class java.lang.String")
                                    && Strings.isBlank((String) getFieldValueByName(field.getName(),obj))){
                                throwAnnotation(field,checkCondition);
                            }else if(getFieldValueByName(field.getName(),obj)==null){
                                throwAnnotation(field,checkCondition);
                            }
                        }else if(checkCondition.checkType() == CheckTypeEnum.IS_GREATER_ZERO){
                            if(field.getGenericType().toString().equals("int")
                                    || field.getGenericType().toString().equals("long")
                                    || field.getGenericType().toString().equals("class java.lang.Integer")
                                    || field.getGenericType().toString().equals("class java.lang.Long")
                                    || field.getGenericType().toString().equals("class java.math.BigDecimal")){
                                Number value = (Number) getFieldValueByName(field.getName(),obj);
                                if(null == value || 0 >= value.longValue()){
                                    throwAnnotation(field,checkCondition);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void throwAnnotation(Field field,CheckCondition checkCondition){
        throw new JKangException(Integer.MAX_VALUE, StringUtils.isNotBlank(checkCondition.errorContent()) ? checkCondition.errorContent()
                : field.getName() + checkCondition.checkType().getErrorMessage());
    }
}