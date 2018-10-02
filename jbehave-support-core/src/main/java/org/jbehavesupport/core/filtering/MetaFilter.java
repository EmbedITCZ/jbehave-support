package org.jbehavesupport.core.filtering;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Setting of metafilter for particular test
 *
 * Ex: @Metafilter("all('api','rest') && all('authentication','plain')")
 * We will use this filter when we want to run scenarios in the story designed for rest api and supporting plain authentication
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
@Inherited
@Documented
public @interface MetaFilter {
    String[] expressions() default {};
}
