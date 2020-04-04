package com.sht.deal.controller;

import com.sht.deal.domain.Goods;
import com.sht.deal.service.GoodsService;
import com.sht.deal.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping({"api/goods"})
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

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
    public ResponseEntity<PageResult<Goods>> findByPage(@RequestParam(value = "id", defaultValue = "") Integer id, //二级分类id
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


}
