package com.rent.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.OrderPayMapper;
import com.rent.dao.PictureMapper;
import com.rent.pojo.base.OrderPay;
import com.rent.pojo.base.Picture;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
/**
 * @author obuivy
 */
@Service
public class UtilsService implements com.rent.service.impl.UtilsImpl {
    private static final List<String> ALLOW_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png");
    @Autowired
    PictureMapper pictureMapper;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    OrderPayMapper orderPayMapper;

    File src = new File("fileSource");
    private String uploadFilePath = src.getAbsolutePath();
    @Override
    public byte[] getPhoto(String filePath) throws IOException {
        File file = new File("fileSource/" + filePath);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }
    @Override
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
    @Override
    public boolean isPictureIdExist(String pictureId){
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<Picture>();
        queryWrapper.eq("picture_id",pictureId);
        List<Picture> pictureList = pictureMapper.selectList(queryWrapper);
        return pictureList.size() != 0;
    }
    @Override
    public ArrayList<String> getPictureUrls(String pictureId, HttpServletRequest request){
        ArrayList<String> list = new ArrayList<String>();
        list.add(pictureId);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<Picture>();
        queryWrapper.eq("picture_id", pictureId);
        List<Picture> pictureList = pictureMapper.selectList(queryWrapper);
        String url = request.getRequestURL().toString();

        for (Picture pictureName :
                pictureList){
            list.add(url.substring(0, url.lastIndexOf('/')) + "/getPicture?pictureName=" + pictureName.getPictureName());
        }
        return list;
    }
    @Override
    public ArrayList<String> uploadFiles(MultipartFile[] pictures){
        ArrayList<String> pictureList = new ArrayList<String>();
        String pictureId = UUID.randomUUID().toString();

        for (MultipartFile f :
                pictures) {
            String fileName = fileUpload(f);
            if(!"500".equals(fileName)){
                pictureList.add(fileName);
            }
        }

        int flag = 0;
        for (String pictureName :
                pictureList) {
            Picture pictureObject = new Picture(pictureId, pictureName);
            try{
                pictureMapper.insert(pictureObject);
            }catch (Exception e){
                e.printStackTrace();
                flag++;
            }
        }

        ArrayList<String> list = new ArrayList<String>();
        list.add(String.valueOf(pictureList.size() - flag));
        list.add(pictureId);
        return list;
    }
    @Override
    public boolean areFilesPicture(MultipartFile[] files){
        for (MultipartFile file :
                files) {
            try {
                // 1.文件校验
                if(file == null){
                    throw new RuntimeException("图片不存在");
                }
                // 2.后缀名校验
                String contentType = file.getContentType();
                System.out.println(contentType);
                if (!ALLOW_IMAGE_TYPES.contains(contentType)) {
                    throw new RuntimeException("图片格式不正确！");
                }
                // 3.内容校验
                try {
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    if (image == null) {
                        // 图片格式不正确
                        throw new RuntimeException();
                    }
                } catch (IOException e) {
                    throw new RuntimeException("图片格式不正确！");
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    @Override
    public List<Picture> getThosePictures(String column, String value){
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<Picture>();
        queryWrapper.eq(column,value);
        return pictureMapper.selectList(queryWrapper);
    }

    public boolean isNowEntpId(int entpId){
        return entpId == enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId();
    }

    /**
     * 获取支付id
     * @param userId 用户id
     * @return 半随机支付id
     */
    public String getRandomPayId(int userId){
        StringBuilder stringBuilder = new StringBuilder();
        Integer integer = orderPayMapper.selectCount(null);
        stringBuilder.append("RPP");
        stringBuilder.append(integer+1);
        stringBuilder.append(UUID.randomUUID().toString().toUpperCase(), 0, 5);
        stringBuilder.append(userId);
        return String.valueOf(stringBuilder);
    }
}
