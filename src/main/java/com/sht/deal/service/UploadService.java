package com.sht.deal.service;

import com.sht.deal.utils.JsonData;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UploadService {
    private static final Logger logger = LoggerFactory.getLogger(com.sht.deal.controller.UploadController.class);


    public JsonData upload(MultipartFile[] fileArray, String site) {
        try {
            if (fileArray == null) {
                return JsonData.buildError("文件不能为空");
            }
            Map result = new HashMap();
            String name = "";
            String url = "";
            //循环上传图片
            for (int i = 0; i < fileArray.length; i++) {
                MultipartFile file = fileArray[i];

                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image == null) {
                    logger.info("上传失败，文件内容不符合");
                    return JsonData.buildError("上传失败，文件内容不符合");
                }
                long size = file.getSize();
                if (size >= 10 * 1024 * 1024) {
                    return JsonData.buildError("文件大小不能超过10M");
                }
                //原图
                String fileName = file.getOriginalFilename();
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                UUID uuid = UUID.randomUUID();
                fileName = uuid + suffixName;

                File dir = new File(site);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                file.transferTo(new File(dir, fileName));

                //缩略图
                String thumbnailName = uuid + "thumbnail" + suffixName;

                String thumbnailUrl = "http://47.93.240.205:8800" + site + "/" + thumbnailName;

                if (size < 204800L) {
                    Thumbnails.of(new String[]{site + "/" + fileName}).scale(1.0D).toFile(site + "/" + thumbnailName);
                } else {
                    double scale = (204800.0F / (float) size);
                    Thumbnails.of(new String[]{site + "/" + fileName}).scale(1.0D).outputQuality(scale).toFile(site + "/" + thumbnailName);
                }
                if (i == 0 ){
                    name += thumbnailName;
                    url += thumbnailUrl;
                }else {
                    name += ","+thumbnailName;
                    url += ","+thumbnailUrl;
                }
            }
            result.put("thumbnailName", name);
            result.put("thumbnailUrl", url);
            return JsonData.buildSuccess(result, "上传成功");
        } catch (Exception e) {

            return JsonData.buildError(e, "上传失败，其他错误");
        }
    }
}
