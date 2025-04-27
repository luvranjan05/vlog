package com.vlogexample.ranvlog.controller;


import com.vlogexample.ranvlog.entity.Users;
import com.vlogexample.ranvlog.requests.LoginRequest;
import com.vlogexample.ranvlog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController

public class UsersController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/health")
    public String health() {
    	return "everything is ok";
    }


    // to add new user basically for registration if anyone register then his data will come to my database through which later on they can login
    @PostMapping("/addUser")
    @CrossOrigin(origins = "http://localhost:3000/")
    public Users addUser(@RequestBody Users user){
        return userService.addUser(user);
    }

    //this is to login which will return true or false so there is boolean
    @PostMapping("/loginUser")
    @CrossOrigin(origins = "http://localhost:3000/")
    public Boolean loginUser(@RequestBody LoginRequest loginRequest){

        return userService.loginUser(loginRequest);
    }

}