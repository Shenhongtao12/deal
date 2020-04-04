package com.sht.deal.service;

import com.sht.deal.utils.JsonData;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
public class UploadService {
    //private static final Logger logger = LoggerFactory.getLogger(UploadService.class);


    public JsonData upload(MultipartFile[] fileArray, String site) {
        try {
            if (fileArray == null) {
                return JsonData.buildError("文件不能为空");
            }
            Map<String, Object> result = new HashMap<>();
            String name = "";
            String url = "";
            //循环上传图片
            for (int i = 0; i < fileArray.length; i++) {
                MultipartFile file = fileArray[i];

                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image == null) {
                    log.error("上传失败，文件内容不符合");
                    return JsonData.buildError("上传失败，文件内容不符合");
                }
                long size = file.getSize();
                if (size >= 10 * 1024 * 1024) {
                    log.error("文件过大：" + size + " 字节");
                    return JsonData.buildError("文件大小不能超过10M");
                }
                //原图
                String fileName = file.getOriginalFilename();
                String suffixName = fileName.substring(fileName.lastIndexOf(".")); //截取图片后缀名
                UUID uuid = UUID.randomUUID();
                fileName = uuid + suffixName;

                File dir = new File(site);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                file.transferTo(new File(dir, fileName));

                //缩略图
                String thumbnailName = uuid + "thumbnail" + suffixName;

                String thumbnailUrl = "http://eurasia.plus:8800" + site + "/" + thumbnailName;

                if (size < 200 * 1024) {
                    Thumbnails.of(new String[]{site + "/" + fileName}).scale(1.0D).toFile(site + "/" + thumbnailName);
                } else if (size < 900 * 1024){
                    Thumbnails.of(new String[]{site + "/" + fileName}).scale(1.0D).outputQuality(0.4f).toFile(site + "/" + thumbnailName);
                }else {
                    Thumbnails.of(new String[]{site + "/" + fileName}).scale(1.0D).outputQuality(0.2f).toFile(site + "/" + thumbnailName);
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

    //删除图片,传入完整的url  http://eurasia.plus:8800/deal/buy/1f33f675-edd6-4277-8daf-4c4f6f3470cethumbnail.png
    public String deleteImage(String url) {
        String resultInfo = null;

        String path = url.substring(24); //http://eurasia.plus:8800

        String name2 = path.substring(0, path.indexOf("thumbnail"));
        String jpg = url.substring(url.lastIndexOf("."));
        String path2 = name2 + jpg;
        File file = new File(path);
        File file2 = new File(path2);
        if (file.exists()) {
            if (file.delete() && file2.delete()) {
                resultInfo = "删除成功";
            } else {
                resultInfo = "删除失败";
                log.error("图片删除失败! " + path2);
            }
        } else {
            resultInfo = "文件不存在";
            log.error("图片不存在"  + path);
        }
        return resultInfo;
    }
}
