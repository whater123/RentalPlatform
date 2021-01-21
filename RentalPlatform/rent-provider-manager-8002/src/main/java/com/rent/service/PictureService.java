package com.rent.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class PictureService {
    File src = new File("fileSource");
    private String uploadFilePath = src.getAbsolutePath();


    public String fileUpload(MultipartFile uploadFile){
        File folder = new File(uploadFilePath);
        if(!folder.isDirectory()){
            folder.mkdirs();
        }
        String oldName = uploadFile.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));
        try{
            uploadFile.transferTo(new File(folder, newName));
            return newName;
        } catch (IOException e) {
            e.printStackTrace();
            return "500";
        }
    }
}
