package com.service.impl;

import com.service.UserService;
import com.spring.Component;
import com.spring.Scope;

@Component("womServiceImpl")
public class WomServiceImpl implements UserService {
    @Override
    public String hello(String msg) {
        System.out.println(msg);
        return "你好userServiceImpl";
    }
}
