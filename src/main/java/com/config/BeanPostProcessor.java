package com.config;

/**
 * bean扩展接口1
 * postProcessBeforeInitialization方法在bean初始化之前执行
 * postProcessAfterInitialization方法在bean初始化之后执行
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName);

    Object postProcessAfterInitialization(Object bean, String beanName);
}
