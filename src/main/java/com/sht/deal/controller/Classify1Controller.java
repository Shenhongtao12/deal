package com.sht.deal.controller;

import com.sht.deal.domain.Classify1;
import com.sht.deal.domain.Classify2;
import com.sht.deal.service.Classify1Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//1.解决跨域
@CrossOrigin
@RestController
@RequestMapping("api/classify1")
public class Classify1Controller {

    @Autowired
    private Classify1Service classify1Service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Classify1 classify1) {
        return ResponseEntity.ok(this.classify1Service.add(classify1));
    }

    @GetMapping("findAll")
    public ResponseEntity<List<Classify1>> findAll() {
        return ResponseEntity.ok(classify1Service.findAll());
    }

    @GetMapping("findById")
    public ResponseEntity<Classify1> findById(Integer id) {
        return ResponseEntity.ok(this.classify1Service.findById(id));
    }

    @GetMapping("delete")
    public ResponseEntity delete(Integer id) {
        return ResponseEntity.ok(this.classify1Service.delete(id));
    }

    @PutMapping("update")
    public ResponseEntity update(@RequestBody Classify1 classify1) {
        return ResponseEntity.ok(this.classify1Service.update(classify1));
    }

    @GetMapping("findChildById")
    public ResponseEntity<List<Classify2>> findChildById(Integer id) throws Exception {
        return ResponseEntity.ok(this.classify1Service.findChildById(id));
    }

    @GetMapping("findClassify1")
    public ResponseEntity<List<Classify1>> findClassify1() {
        return ResponseEntity.ok(this.classify1Service.findClassify1());
    }

}
