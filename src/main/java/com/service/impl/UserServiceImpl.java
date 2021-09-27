package com.service.impl;

import com.service.UserService;
import com.spring.Component;
import com.spring.Resource;

@Component("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Resource(required = true)
    WomServiceImpl womServiceImpl;
    //StudentServiceImpl studentServiceImpl;

    private String msg;

  /*  public void studentServiceImpl(String msg) {
        this.msg = studentServiceImpl.reply(msg);
        hello(this.msg);
    }*/

    public void test(){
        System.out.println(womServiceImpl);
    }

    @Override
    public String hello(String msg) {
        System.out.println("嗨：同学好"+msg);
        return "嗨：同学好"+msg;
    }

}
