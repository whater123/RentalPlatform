package com.rent.controller;

import com.rent.pojo.view.ReturnMsg;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author w
 */
@RestController
public class ShiroController {
    @GetMapping("/toLogin")
    public ReturnMsg toLogin(){
        return new ReturnMsg("301",true);
    }

    @GetMapping("/notRole")
    public ReturnMsg notRole(){
        return new ReturnMsg("303",true,"权限不足！");
    }
}
