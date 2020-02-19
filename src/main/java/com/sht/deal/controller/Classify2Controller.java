package com.sht.deal.controller;

import com.sht.deal.domain.Classify2;
import com.sht.deal.service.Classify2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"api/classify2"})
public class Classify2Controller {

    @Autowired
    private Classify2Service classify2Service;

    @PostMapping({"add"})
    public ResponseEntity add(@RequestBody Classify2 classify2) {
        return ResponseEntity.ok(this.classify2Service.add(classify2));
    }

    @GetMapping({"delete"})
    public ResponseEntity delete(Integer id) {
        return ResponseEntity.ok(this.classify2Service.delete(id));
    }


    @PutMapping({"update"})
    public ResponseEntity update(@RequestBody Classify2 classify2) {
        return ResponseEntity.ok(this.classify2Service.update(classify2));
    }
}
