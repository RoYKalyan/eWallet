package com.project.ePocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService service;

    @PostMapping("/user")
    public void createUser(@RequestBody User user) throws JsonProcessingException {
      service.createUser(user);
    }

    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable("userId") String userId)
    {
        return service.getUser(userId);
    }



}
