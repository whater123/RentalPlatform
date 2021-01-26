package com.rent.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MSI-PC
 */
@RestController
public class MyController {
    @RequestMapping("/")
    public String rsndm(){
        return "燃烧你的梦";
    }
}
