package com.sht.deal.controller;

import com.sht.deal.domain.Comment;
import com.sht.deal.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"api/token/comment"})
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping({"save"})
    public ResponseEntity save(@RequestBody Comment comment) throws Exception {
        return ResponseEntity.ok(this.commentService.save(comment));
    }

    @DeleteMapping({"delete"})
    public ResponseEntity delete(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.ok(this.commentService.delete(id));
    }
}
