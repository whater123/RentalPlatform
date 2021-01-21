package com.rent.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.rent.pojo.view.ReturnMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/manager")
public class RegisiterAndLoginController {

    @RequestMapping("/toRegister")
    public ReturnMsg toRegister(){
        return new ReturnMsg();
    }

    @RequestMapping("/toLogin")
    public ReturnMsg toLogin(){
        return new ReturnMsg();
    }
}
