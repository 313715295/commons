package com.jdxiaokang.core.utils;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class OrikaMappingUtil {

    private static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public static MapperFactory getMapperFactory(){
        return mapperFactory;
    }

    /**
     * @return a new default instance of MapperFactory
     */
    public static MapperFacade getInstance() {
        return mapperFactory.getMapperFacade();
    }

    public static <T> List<T> map(List<?> objects, Class<T> target) {
        if (CollectionUtils.isEmpty(objects)) {
            return Collections.EMPTY_LIST;
        }
        return mapperFactory.getMapperFacade().mapAsList(objects.toArray(), target);
    }
}
