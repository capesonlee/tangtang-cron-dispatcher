package com.lijuyong.startup.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by john on 2017/3/30.
 */
@RestController
public class TaskController {
    @RequestMapping("/hello")
    public String hello(){
        return "hello to you";
    }
}
