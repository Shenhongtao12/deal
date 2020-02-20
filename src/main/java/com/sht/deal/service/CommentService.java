package com.sht.deal.service;

import com.sht.deal.Mapper.CommentMapper;
import com.sht.deal.Mapper.LikeMapper;
import com.sht.deal.domain.Comment;
import com.sht.deal.exception.AllException;
import com.sht.deal.service.GoodsService;
import com.sht.deal.service.ReplyService;
import com.sht.deal.utils.DateUtils;
import com.sht.deal.utils.JsonData;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public List<Comment> findByGoodsId(int id, int userid) {
        List<Comment> commentList = this.commentMapper.findByGoodsId(id);
        for (Comment comment : commentList) {
            comment.setState(this.likeMapper.findLoveBy("comment", comment.getCommentid(), userid));
            comment.setReplyList(this.replyService.getTreeReply(comment.getCommentid(), userid));
        }
        return commentList;
    }


    public JsonData save(Comment comment) throws Exception {
        if (comment.getContent().equals("")) {
            throw new AllException(-1, "内容不能为空");
        }
        if (comment.getContent().getBytes("UTF-8").length > 200) {
            throw new AllException(-1, "回复内容过长");
        }
        comment.setCreatetime(DateUtils.dateByString());
        if (comment.getGoodsid() == null || comment.getUserid() == null) {
            throw new AllException(-1, "用户id或商品id不能为空");
        }
        int i = this.commentMapper.insertSelective(comment);
        if (i != 1) {
            return JsonData.buildError("留言失败");
        }

        int userId = this.goodsService.findUserIdByGoodsId(comment.getGoodsid());
        if (comment.getUserid() != userId) {
            this.redisTemplate.boundValueOps(String.valueOf(userId)).increment(1L);
        }
        return JsonData.buildSuccess("留言成功");
    }


    public JsonData delete(Integer id) {
        this.replyService.deleteByCommentId(id);

        int i = this.commentMapper.deleteByPrimaryKey(id);
        if (i != 1) {
            return JsonData.buildError("删除失败");
        }
        return JsonData.buildSuccess("删除成功");
    }
}
