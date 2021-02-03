package com.rent.controller;

import com.alibaba.fastjson.JSON;
import com.rent.dao.PictureMapper;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.UtilsService;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author obuivy
 */
@RestController
@RequestMapping("/manager/utils")
public class UtilsController {
    @Autowired
    UtilsService utilsService;
    @Autowired
    PictureMapper pictureMapper;

    @RequestMapping(path = "/uploadPictures",produces = "application/json;charset=UTF-8")
    public ReturnMsg uploadPictures(MultipartFile[] pictures, HttpServletRequest request){
        if(pictures.length == 0){
            return new ReturnMsg("401",true,"参数不齐");
        }
        try{
            if(!utilsService.isFilesPicture(pictures)){
                return new ReturnMsg("402",true,"参数不合法");
            }
            ArrayList<String> list = utilsService.uploadFiles(pictures);
            return new ReturnMsg("200",false,
                    "共计" + pictures.length + "个文件，成功上传" + list.get(0) + "个图片",
                    utilsService.getPictureUrls(list.get(1), request));
        } catch (Exception e) {
            return new ReturnMsg("500",true,"上传失败");
        }
    }

    @RequestMapping(path = "/uploadPicture",produces = "application/json;charset=UTF-8")
    public ReturnMsg uploadPicture(MultipartFile picture, HttpServletRequest request){
        if(picture == null){
            return new ReturnMsg("401",true,"参数不齐");
        }
        MultipartFile[] pictures = new MultipartFile[1];
        pictures[0] = picture;
        return uploadPictures(pictures,request);
    }

    @RequestMapping(value = "/getPicture", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPicture(String pictureName) throws IOException {
        return utilsService.getPhoto(pictureName);
    }

    @RequestMapping(path = "/getPictureUrlList",produces = "application/json;charset=UTF-8")
    public ReturnMsg uploadPicture(@RequestBody String json, HttpServletRequest request){
        if(MyUtil.jsonHasVoid(json,"pictureId")){
            return new ReturnMsg("401",true,"参数不齐");
        }
        String pictureId = JSON.parseObject(json).getString("pictureId");
        if(!utilsService.isPictureIdExist(pictureId)){
            return new ReturnMsg("403",true,"所查找的图组id不存在");
        }
        return new ReturnMsg("200",false,"查询成功", utilsService.getPictureUrls(pictureId,request));
    }

}
