package com.jdxiaokang.commons.core.annotation.elasticsearch;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Demarcates an identifier.
 *
 * @author Jon Brisbin
 * @author Oliver Gierke
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD, METHOD, ANNOTATION_TYPE })
public @interface Id {
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
}