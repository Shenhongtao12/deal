package com.sht.deal.controller;

import com.sht.deal.domain.Buy;
import com.sht.deal.service.BuyService;
import com.sht.deal.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/token/buy")
public class BuyController {
    @Autowired
    private BuyService buyService;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Buy buy) throws Exception {
        return ResponseEntity.ok(this.buyService.add(buy));
    }

    @GetMapping("findByPage")
    public ResponseEntity<PageResult<Buy>> findByPage(
            @RequestParam(value = "userid", defaultValue = "") Integer userid,
            @RequestParam(value = "buyName", required = false) String buyName,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "10") Integer rows
    ) {
        return ResponseEntity.ok(buyService.findByPage(userid, buyName, page, rows));
    }


    @DeleteMapping("delete")
    public ResponseEntity delete(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.ok(buyService.delete(id));
    }


    @PutMapping({"update"})
    public ResponseEntity update(@RequestBody Buy buy) throws Exception {
        return ResponseEntity.ok(this.buyService.update(buy));
    }

    @GetMapping({"findByLike"})
    public ResponseEntity findByLike(@RequestParam("title") String title, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return ResponseEntity.ok(this.buyService.findByLike(title, page, rows));
    }
}


