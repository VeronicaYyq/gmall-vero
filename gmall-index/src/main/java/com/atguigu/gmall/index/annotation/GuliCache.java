package com.atguigu.gmall.index.annotation;


import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented

public @interface GuliCache {

    String prefix() default "";
}
