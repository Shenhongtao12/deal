package com.sht.deal.controller;

import com.sht.deal.domain.Collect;
import com.sht.deal.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/token/collect"})
public class CollectController {
    @Autowired
    private CollectService collectService;

    @PostMapping({"/save"})
    public ResponseEntity save(@RequestBody Collect collect) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.collectService.save(collect));
    }

    @DeleteMapping({"/delete"})
    public ResponseEntity delete(@RequestParam(name = "ids") Integer[] ids) {
        return ResponseEntity.ok(this.collectService.delete(ids));
    }


    @GetMapping({"/findByUser"})
    public ResponseEntity findByUser(@RequestParam(name = "id") Integer id, @RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "rows", defaultValue = "10") Integer rows) {
        return ResponseEntity.ok(this.collectService.findByUser(id, page, rows));
    }
}
