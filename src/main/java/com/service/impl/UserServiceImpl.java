package com.service.impl;

import com.config.BeanNameAware;
import com.config.BeanPostProcessor;
import com.config.InitializingBean;
import com.service.UserService;
import com.spring.Component;
import com.spring.Resource;
import com.start.LiXiaoFengApplication;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component("userServiceImpl")
public class UserServiceImpl implements UserService, BeanNameAware , InitializingBean , BeanPostProcessor {

    @Resource(required = true)
    WomServiceImpl womServiceImpl;
    //StudentServiceImpl studentServiceImpl;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;

    private String beanName;

  /*  public void studentServiceImpl(String msg) {
        this.msg = studentServiceImpl.reply(msg);
        hello(this.msg);
    }*/

    public String test(){
        String reply = womServiceImpl.hello("你好womServiceImpl");
        return reply;
    }

    @Override
    public String hello(String msg) {
        System.out.println("嗨：同学好"+msg);
        return "嗨：同学好"+msg;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化"+beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        ((UserServiceImpl)bean).setMsg("初始化之前的设置");
        System.out.println("初始化之前");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        Object newProxyInstance = Proxy.newProxyInstance(UserServiceImpl.class.getClassLoader(), bean.getClass().getInterfaces(),(proxy, method, args) ->{
            System.out.println("生成代理对象");
            return method.invoke(bean,args);
        });

        System.out.println("初始化之后执行");
        return newProxyInstance;
    }
}
