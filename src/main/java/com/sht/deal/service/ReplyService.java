package com.sht.deal.service;

import com.sht.deal.Mapper.LikeMapper;
import com.sht.deal.Mapper.ReplyMapper;
import com.sht.deal.domain.Reply;
import com.sht.deal.exception.AllException;
import com.sht.deal.service.ReplyService;
import com.sht.deal.utils.DateUtils;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


@Service
@Transactional
public class ReplyService {
    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void deleteByCommentId(int comid) {
        Example example = new Example(Reply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("commentid", comid);
        this.replyMapper.deleteByExample(example);
    }


    public JsonData save(Reply reply) throws Exception {
        if (reply.getCommentid() == null || reply.getLeaf() == null) {
            throw new AllException(-1, "回复失败");
        }
        if (reply.getUserid() == null) {
            throw new AllException(-1, "请登录！");
        }
        if (reply.getContent().equals("") || reply.getContent().equals(" ")) {
            throw new AllException(-1, "内容不能为空");
        }
        if (reply.getContent().getBytes("UTF-8").length > 200) {
            throw new AllException(-1, "内容过长");
        }
        reply.setCreatetime(DateUtils.dateByString());

        if (reply.getLeaf() != 0) {
            this.replyMapper.updateLeaf(reply.getLeaf());
            reply.setParentid(reply.getLeaf());
        }
        reply.setParentid(reply.getLeaf());
        reply.setLeaf(0);
        int i = this.replyMapper.insertSelective(reply);
        if (i != 1) {
            return JsonData.buildError("回复失败");
        }

        if (!reply.getUserid().equals(reply.getNameid())) {
            this.redisTemplate.boundValueOps(String.valueOf(reply.getNameid())).increment(1);
        }
        return JsonData.buildSuccess("回复成功");
    }

    public JsonData delete(Integer id) {
        int i = this.replyMapper.deleteByPrimaryKey(id);
        if (i != 1) {
            throw new AllException(-1, "失败");
        }
        return JsonData.buildSuccess("成功");
    }


    public JsonData findAllByUser(Integer userId) {
        List<MessageUtils> list = this.replyMapper.findReply(userId);
        for (MessageUtils messageUtils : list) {
            MessageUtils goods = this.replyMapper.findGoods(messageUtils.getGoodsid());
            String[] images = goods.getImages().split(",");
            messageUtils.setImages(images[0]);
            messageUtils.setName(goods.getName());
        }

        List<MessageUtils> commentList = new ArrayList<MessageUtils>();
        int[] ids = this.replyMapper.findGoodsId(userId);
        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            commentList = this.replyMapper.findComment(id, userId);
            for (MessageUtils messageUtils : commentList) {
                MessageUtils goods = this.replyMapper.findGoods(messageUtils.getGoodsid());
                String[] images = goods.getImages().split(",");
                messageUtils.setImages(images[0]);
                messageUtils.setName(goods.getName());
            }
        }
        if (commentList.size() > 0) {
            list.addAll(commentList);
        }
        if (list.size() == 0) {
            return JsonData.buildError("无数据");
        }else {
            Collections.sort(list);
            redisTemplate.delete(String.valueOf(userId));
            return JsonData.buildSuccess(list, "");
        }
    }


    public List<Reply> getTreeReply(int id, int userid) {
        List<Reply> list = this.replyMapper.findByComId(id);
        for (Reply reply : list) {
            reply.setState(this.likeMapper.findLoveBy("reply", reply.getId(), userid));
        }

        connectReply(list);

        List<Reply> rootReply = getRootReply(list);

        List<Reply> result = new ArrayList<Reply>();


        for (Reply reply : rootReply) {
            addReplyToResult(result, reply);
        }
        return result;
    }


    private void connectReply(List<Reply> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            Reply replyLast = (Reply) list.get(i);
            List<Reply> replyList = new ArrayList<Reply>();
            List<Reply> rList = new ArrayList<Reply>();
            for (int j = i + 1; j < list.size(); j++) {
                Reply replyNext = (Reply) list.get(j);
                if (replyNext.getParentid().equals(replyLast.getId())) {
                    replyList.add(replyNext);
                } else if (replyNext.getId().equals(replyLast.getParentid())) {
                    rList.add(replyLast);
                    replyNext.setReplyList(rList);
                }
            }
            replyLast.setReplyList(replyList);
        }
    }


    private List<Reply> getRootReply(List<Reply> list) {
        List<Reply> rootReply = new ArrayList<Reply>();
        for (Reply reply : list) {
            if (reply.getParentid() == 0) {
                rootReply.add(reply);
            }
        }
        return rootReply;
    }


    private void addReplyToResult(List<Reply> result, Reply reply) {
        result.add(reply);
        if (reply.getLeaf() == 0) {
            return;
        }
        List<Reply> list = reply.getReplyList();
        for (Reply reply1 : list) {
            if (reply1.getParentid() == 0)
                addReplyToResult(result, reply1);
        }
    }
}
