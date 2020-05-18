package com.sht.deal.controller;

import com.sht.deal.domain.Reply;
import com.sht.deal.service.ReplyService;
import com.sht.deal.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"api/token/reply"})
public class ReplyController {

    @Autowired
    private ReplyService replyService;
    @PostMapping({"save"})
    public ResponseEntity save(@RequestBody Reply reply) throws Exception {
        return ResponseEntity.ok(this.replyService.save(reply));
    }

    @DeleteMapping({"delete"})
    public ResponseEntity delete(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.ok(this.replyService.delete(id));
    }


    @GetMapping({"findAllByUser"})
    public ResponseEntity<JsonData> findAllByUser(@RequestParam(name = "nameId") Integer nameId) {
        return ResponseEntity.ok(this.replyService.findAllByUser(nameId));
    }
}
