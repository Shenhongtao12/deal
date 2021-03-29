package com.sht.deal.controller;

import com.sht.deal.domain.Post;
import com.sht.deal.service.CommentService;
import com.sht.deal.service.PostService;
import com.sht.deal.service.UserService;
import com.sht.deal.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aaron
 * @date 2020/5/17 - 19:07
 **/
@RestController
@RequestMapping("/api/post")
@Api(tags = "帖子服务")
public class PostController{

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @PostMapping()
    @ApiOperation(value = "发布新帖子")
    public ResponseEntity<JsonData> save(@RequestBody Post post){
        post.setUserId(post.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(postService.save(post));
    }

    @PutMapping()
    public ResponseEntity<JsonData> update(@RequestBody Post post) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.update(post));
    }

    @DeleteMapping
    @ApiOperation(value = "删除帖子")
    public ResponseEntity<JsonData> delete(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.delete(id));
    }

    @GetMapping("findById")
    @ApiOperation(value = "根据id查看帖子详情")
    public ResponseEntity<JsonData> findById(@RequestParam(name = "postId") Integer postId,
                                             @RequestParam(name = "userId") Integer userId){
        Post result = postService.findById(postId, userId);
        if (result == null) {
            return ResponseEntity.ok(JsonData.buildError("不存在的postId：" + postId));
        }
        return ResponseEntity.ok(JsonData.buildSuccess(result, ""));
    }

    @GetMapping("findByPage")
    @ApiOperation(value = "多条件查询")
    public ResponseEntity<JsonData> findByClassify(
            @RequestParam(name = "searchName",required = false) String searchName,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "rows", defaultValue = "30") Integer rows
            ) {
        return ResponseEntity.ok(JsonData.buildSuccess(postService.findByClassify(searchName, page, rows), ""));
    }

    @GetMapping("findByUserId")
    @ApiOperation(value = "根据userId查询所属帖子")
    public ResponseEntity<JsonData> findByUserId(@RequestParam(name = "id",required = false) Integer id){
        Map<String, Object> map = new HashMap<>();
        List<Post> byUserId = postService.findByUserId(id);
        /*for (Post post : byUserId) {
            post.setCommentNum(commentService.countByPostId(post.getId()));
        }*/
        map.put("postList", byUserId);
        map.put("user", userService.findById(id));
        return ResponseEntity.ok(JsonData.buildSuccess(map,""));
    }

    //@GetMapping("findByFansUserId")
    //@ApiOperation(value = "关注的人发布的帖子")
    /*public ResponseEntity<JsonData> findByFansUserId(){
        return ResponseEntity.ok(JsonData.buildSuccess(postService.findByFansUserId(userId),""));
    }*/


}
