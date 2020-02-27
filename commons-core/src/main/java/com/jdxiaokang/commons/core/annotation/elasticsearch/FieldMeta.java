package com.jdxiaokang.commons.core.annotation.elasticsearch;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Artur Konczak
 * @author Jonathan Yan
 * @author Jakub Vavrik
 * @author Kevin Leturc
 * @author Peter-Josef Meisch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface FieldMeta {

    /**
     * Alias for {@link #name}.
     * @since 3.2
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The <em>name</em> to be used to store the field inside the document.
     * <p>If not set, the name of the annotated property is used.
     * @since 3.2
     */
    @AliasFor("value")
    String name() default "";

    FieldType type() default FieldType.Auto;

    boolean index() default true;

    DateFormat format() default DateFormat.none;

    String pattern() default "";

    boolean store() default false;

    boolean fielddata() default false;

    String searchAnalyzer() default "";

    String analyzer() default "";

    String normalizer() default "";

    String[] ignoreFields() default {};

    boolean includeInParent() default false;

    String[] copyTo() default {};
}
