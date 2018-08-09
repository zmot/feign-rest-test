package com.tm.feignresttest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NameController {

    @GetMapping("/word")
    public @ResponseBody String word(){
        return "word";
    }
}
