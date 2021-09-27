package com.config;

/**
 * bean初始化方式
 */
public interface InitializingBean {

    void afterPropertiesSet() throws Exception;
}
