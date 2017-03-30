package com.lijuyong.startup.web;

import com.lijuyong.startup.service.TaskDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by john on 2017/3/30.
 */
@RestController
public class TaskController {

    @Autowired
    TaskDispatcher taskDispatcher;

    @RequestMapping("/auto")
    public String auto(){
        String expression = "0/30 * * * * ?";
        taskDispatcher.addNewIndicator(expression);
        return "hello to you";
    }

    @RequestMapping("/manual")
    public String manual(){
        taskDispatcher.manualInvokeTask();
        return "we will fire a task manually";

    }
}
