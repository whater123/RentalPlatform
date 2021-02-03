package com.rent.service.impl;

import com.rent.pojo.base.Picture;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author obuivy
 */
public interface UtilsImpl {

    /**
     * @param filePath 该文件在fileSource中所处路径
     * @return  文件数据的byte数组
     * @throws IOException 文件输出流报错
     */
    byte[] getPhoto(String filePath) throws IOException;

    /**
     * @param uploadFile 需上传的文件
     * @return 该文件的新名字或"500"，（新名字由UUID+后缀构成）
     */
    String fileUpload(MultipartFile uploadFile);

    /**
     * @param pictureId 图组id
     * @return 该图组id是否存在
     */
    boolean isPictureIdExist(String pictureId);

    /**
     * @param pictureId 图组id
     * @param request HttpServletRequest
     * @return 该图组id下的所有图片的url
     */
    ArrayList<String> getPictureUrls(String pictureId, HttpServletRequest request);

    /**
     * @param pictures 文件数组
     * @return 数组第一个为图组id，其余后项为该图组id下的所有图片url
     */
    ArrayList<String> uploadFiles(MultipartFile[] pictures);

    /**
     * @param files 文件数组
     * @return 判断该文件数组下是否所有文件都为图片
     */
    boolean isFilesPicture(MultipartFile[] files);

    /**
     * @param column 数据库的属性
     * @param value 值
     * @return 返回数据库中column项为value的所有值
     */
    List<Picture> getThosePictures(String column, String value);
}
