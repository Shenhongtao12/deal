package com.sht.deal.controller;

import com.sht.deal.domain.Goods;
import com.sht.deal.service.GoodsService;
import com.sht.deal.service.UploadService;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping({"api/goods"})
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UploadService uploadService;

    @PostMapping({"add"})
    public ResponseEntity add(@RequestBody Goods goods) throws Exception {
        return ResponseEntity.ok(this.goodsService.add(goods));
    }


    @GetMapping({"findById"})
    public ResponseEntity<Goods> findById(@RequestParam("id") Integer id, @RequestParam(value = "userid", defaultValue = "1") Integer userid) throws Exception {
        return ResponseEntity.ok(this.goodsService.findById(id, userid));
    }


    @PutMapping({"update"})
    public ResponseEntity update(@RequestBody Goods goods) throws Exception {
        return ResponseEntity.ok(this.goodsService.update(goods));
    }


    @GetMapping({"findByPage"})
    public ResponseEntity<PageResult<Goods>> findByPage(@RequestParam(value = "id", defaultValue = "") Integer id,
                                                        @RequestParam(value = "classify1", defaultValue = "") Integer classify1,
                                                        @RequestParam(value = "orderBy", defaultValue = "") String orderBy,
                                                        @RequestParam(value = "userid", defaultValue = "") Integer userid,
                                                        @RequestParam(value = "goodsName", required = false) String goodsName,
                                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(value = "rows", defaultValue = "20") Integer rows) {
        return ResponseEntity.ok(this.goodsService.findByPage(id, classify1, orderBy, userid, goodsName, page, rows));
    }


    @GetMapping({"deleteGoods"})
    public ResponseEntity deleteGoods(Integer id) {
        return ResponseEntity.ok(this.goodsService.deleteGoods(id));
    }


    @GetMapping({"findByLike"})
    public ResponseEntity<PageResult<Goods>> findByLike(@RequestParam("goodsName") String goodsName, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "rows", defaultValue = "20") Integer rows) {
        return ResponseEntity.ok(this.goodsService.findByLike(goodsName, page, rows));
    }


    //@PostMapping(value = "image")
    @PostMapping(value = "/image", headers = "content-type=multipart/form-data")
    public ResponseEntity uploadImage(@RequestParam(value = "file") MultipartFile[] file, @RequestParam(name = "site", defaultValue = "/deal/goods") String site) {
        JsonData url = this.uploadService.upload(file, site);
        return ResponseEntity.ok(url);
    }


    @PostMapping({"deleteFile"})
    public ResponseEntity<String> delFile(String name) {
        return ResponseEntity.ok(this.goodsService.deleteImage(name));
    }
}
