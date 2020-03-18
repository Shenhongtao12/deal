package com.sht.deal.controller;

import com.sht.deal.domain.Fans;
import com.sht.deal.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/token/fans"})
public class FansController {

    @Autowired
    private FansService fansService;

    @PostMapping({"save"})
    public ResponseEntity save(@RequestBody Fans fans) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.fansService.save(fans));
    }

    @DeleteMapping({"delete"})
    public ResponseEntity delete(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.ok(this.fansService.delete(id));
    }


    @GetMapping({"findFansToUser"})
    public ResponseEntity findFansToUser(@RequestParam(name = "userId", required = false) Integer userId, @RequestParam(name = "fansId", required = false) Integer fansId, @RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "rows", defaultValue = "10") Integer rows) {
        return ResponseEntity.ok(this.fansService.findFansToUser(userId, fansId, page, rows));
    }


    @GetMapping({"checkFans"})
    public ResponseEntity checkFans(@RequestParam(name = "userId") Integer userId,
                                    @RequestParam(name = "toUserId") Integer toUserId) {
        return ResponseEntity.ok(this.fansService.checkFans(userId, toUserId));
    }
}
