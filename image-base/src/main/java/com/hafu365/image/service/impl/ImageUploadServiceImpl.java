package com.hafu365.image.service.impl;

import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.image.config.UploadConfig;
import com.hafu365.image.entity.UploadInfo;
import com.hafu365.image.service.ImageUploadService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by SunHaiyang on 2017/8/23.
 */
@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    @Autowired
    private UploadConfig uploadConfig;

    private SimpleDateFormat folder = new SimpleDateFormat("yyyyMM");

    @Override
    public UploadInfo upload(MultipartFile multipartFile) {
        UploadInfo uploadInfo = new UploadInfo();
        Date date = new Date();
        String dirPath = folder.format(date);
        String imageName = null;
        if(!multipartFile.isEmpty()){
            try {
                long fileSize = multipartFile.getSize();
                uploadInfo.setSize(fileSize);
                String imageType = isImage(multipartFile.getInputStream());
                if(StringUtils.isNotEmpty(imageType)){
                    uploadInfo.setSuffix(imageType);
                    createDirectory(uploadConfig.FILE_PATH + "\\" + dirPath);
                    imageName = UUID.randomUUID().toString()+"."+imageType;
                    File file = new File(uploadConfig.FILE_PATH + "\\" + dirPath+"\\"+imageName);
                    if(!file.exists())
                        file.createNewFile();
                    FileUtils.writeByteArrayToFile(file,multipartFile.getBytes());
                    uploadInfo.setUrl(uploadConfig.BASE_URL+dirPath+"/"+imageName);
                    return uploadInfo;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 判断路径是否创建
     * @param path
     * @return
     */
    public boolean createDirectory(String path) {
        boolean falg = true;
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }else{
            if(!file.isDirectory()){
                file.mkdirs();
            }
        }
        return falg;
    }

    /**
     * 验证是否是图片
     * @param inputStream
     * @return
     */
    public String isImage(InputStream inputStream){
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
            Iterator<ImageReader> imgRead = ImageIO.getImageReaders(iis);
            if(!imgRead.hasNext()){
                return null;
            }
            ImageReader imageReader = imgRead.next();
            iis.close();
            return imageReader.getFormatName();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
