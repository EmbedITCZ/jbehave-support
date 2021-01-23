package org.jbehavesupport.core.internal;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {

    String value() default "";

    String prefix() default "";

    String name() default "";

}
