package com.zeyt.springboot.taskmanager.controller;

import com.zeyt.springboot.taskmanager.model.User;
import com.zeyt.springboot.taskmanager.service.AppService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppService appService;

    public AuthController(AppService appService) {
        this.appService = appService;
    }

    @PostMapping("/new-user")
    public String addUser(@RequestBody User user) {
        appService.addUser(user);
        return "User is saved";
    }
}
