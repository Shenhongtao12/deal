package com.sht.deal.controller;

import com.sht.deal.service.LikeService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/token/like")
public class LikeController {
	@PostMapping({"save"})
	public ResponseEntity save(@RequestBody Map map) { // "type":"reply:600:600","state":"1"
		this.likeService.save(map.get("type"), map.get("state"));
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Autowired
	private LikeService likeService;
}
