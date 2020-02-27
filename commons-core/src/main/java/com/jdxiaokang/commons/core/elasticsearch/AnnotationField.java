package com.jdxiaokang.commons.core.elasticsearch;

import com.jdxiaokang.commons.core.annotation.elasticsearch.FieldMeta;
import com.jdxiaokang.commons.core.annotation.elasticsearch.Id;
import lombok.Data;

import java.lang.reflect.Field;

@Data
public class AnnotationField {
    private boolean primaryId;
    private Id idMeta;
    private FieldMeta fieldMeta;
    private Field field;
    private String name;
    private Class<?> type;

    public AnnotationField(){}

    public AnnotationField(Id idMeta, Field field) {
        super();
        this.primaryId = Boolean.TRUE;
        this.idMeta = idMeta;
        this.field = field;
        this.name=field.getName();
        this.type=field.getType();
    }

    public AnnotationField(FieldMeta fieldMeta, Field field) {
        super();
        this.fieldMeta = fieldMeta;
        this.field = field;
        this.name=field.getName();
        this.type=field.getType();
    }

    public AnnotationField(FieldMeta fieldMeta, String name, Class<?> type) {
        super();
        this.fieldMeta = fieldMeta;
        this.name = name;
        this.type = type;
    }
}