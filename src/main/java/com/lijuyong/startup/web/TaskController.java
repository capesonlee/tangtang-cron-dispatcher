package com.lijuyong.startup.web;

import com.lijuyong.startup.service.DcsSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by john on 2017/3/30.
 */
@RestController
public class TaskController {

    @Autowired
    DcsSetting dcsSetting;

    @RequestMapping("/auto")
    public String auto(){
        String expression = "0/30 * * * * ?";
        dcsSetting.addNewIndicator(expression);
        return "hello to you";
    }

    @RequestMapping("/manual")
    public String manual(){
        dcsSetting.manualInvokeTask();
        return "we will fire a task manually";

    }
}
