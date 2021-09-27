package com.service.impl;

import com.service.StudentService;
import com.spring.Component;

@Component("studentServiceImpl")
public class StudentServiceImpl implements StudentService {
    @Override
    public String reply(String msg) {
        System.out.println("老师你好："+msg);
        return "老师你好："+msg;
    }
}
