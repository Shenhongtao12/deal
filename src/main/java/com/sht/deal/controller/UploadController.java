package com.sht.deal.controller;

import com.sht.deal.exception.AllException;
import com.sht.deal.service.UploadService;
import com.sht.deal.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//上传图片
@RestController
@RequestMapping({"api/upload"})
public class UploadController {
    @Autowired
    private UploadService uploadService;

    //@PostMapping(value = "/image", headers = "content-type=multipart/form-data")
    @PostMapping({"image"})
    public ResponseEntity uploadImage(@RequestParam("file") MultipartFile[] file, @RequestParam(name = "site", defaultValue = "/deal/other") String site) {
        JsonData url = this.uploadService.upload(file, site);
        if (StringUtils.isEmpty(url)) {
            throw new AllException(-1, "图片上传失败");
        }

        return ResponseEntity.ok(url);
    }

    @PostMapping({"deleteImage"})
    public ResponseEntity<String> delFile(@RequestParam(name = "url") String url) {
        return ResponseEntity.ok(this.uploadService.deleteImage(url));
    }
}
