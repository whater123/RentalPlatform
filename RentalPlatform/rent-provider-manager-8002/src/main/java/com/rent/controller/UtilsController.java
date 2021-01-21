package com.rent.controller;

import com.rent.dao.PictureMapper;
import com.rent.pojo.base.Picture;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/manager/utils")
public class UtilsController {
    @Autowired
    PictureService pictureService;

    @Autowired
    PictureMapper pictureMapper;

    @RequestMapping(path = "/uploadPhotos",produces = "application/json;charset=UTF-8")
    public ReturnMsg uploadPhotos(MultipartFile[] photos){
        ArrayList<String> pictureList = new ArrayList<String>();
        for (MultipartFile f :
                photos) {
            String fileName = pictureService.fileUpload(f);
            if(!"500".equals(fileName)){
                pictureList.add(fileName);
            }
        }
        String pictureId = UUID.randomUUID().toString();
        for (String pictureName :
                pictureList) {
            Picture picture = new Picture(pictureId, pictureName);
            pictureMapper.insert(picture);
        }


        return new ReturnMsg();
    }
    @RequestMapping("/")
    public String nmsl(){
        return "nmsl";
    }

}
