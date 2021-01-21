package com.rent.controller;

import com.rent.pojo.view.ReturnMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager/enterprise")
public class EnterpriseController {

    @RequestMapping("/toAuthentication")
    public ReturnMsg toAuthentication(){
        return new ReturnMsg();
    }
}
