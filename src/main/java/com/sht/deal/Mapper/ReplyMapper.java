package com.sht.deal.Mapper;

import com.sht.deal.domain.Love;
import com.sht.deal.domain.Reply;
import com.sht.deal.utils.MessageUtils;

import java.util.List;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface ReplyMapper extends Mapper<Reply> {
    @Select({"select * from reply where commentid = #{id}"})
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "createtime", column = "createtime"),
            @Result(property = "content", column = "content"),
            @Result(property = "number", column = "number"),
            @Result(property = "leaf", column = "leaf"),
            @Result(property = "userid", column = "userid"),
            @Result(property = "goodsid", column = "goodsid"),
            @Result(property = "commentid", column = "commentid"),
            @Result(property = "nameid", column = "nameid"),
            @Result(property = "parentname", column = "parentname"),
            @Result(property = "parentid", column = "parentid"),
            @Result(property = "user", column = "userid", many = @Many(select = "com.sht.deal.Mapper.UserMapper.findById"))})
    List<Reply> findByComId(int id);

    @Update({"update reply set leaf = 1 where id = #{id}"})
    void updateLeaf(Integer id);

    @Select({"select goodsid, createtime, userid, content from reply where nameid = #{userId} and userid != #{userId}"})
    @Results({@Result(id = true, property = "id", column = "id"),
            @Result(property = "createtime", column = "createtime"),
            @Result(property = "content", column = "content"),
            @Result(property = "userid", column = "userid"),
            @Result(property = "goodsid", column = "goodsid"),
            @Result(property = "user", column = "userid", many =
            @Many(select = "com.sht.deal.Mapper.UserMapper.findById"))})
    List<MessageUtils> findReply(Integer userId);

    @Select({"select name, images from goods where id = #{goodsid}"})
    MessageUtils findGoods(Integer goodsid);

    @Select({"select id from goods where userid = #{userId}"})
    int[] findGoodsId(Integer userId);

    @Select({"select goodsid, createtime, content, userid from comment where goodsid = #{goodsid} and userid != #{userId}"})
    @Results({
            @Result(id = true, property = "commentid", column = "commentid"),
            @Result(property = "createtime", column = "createtime"),
            @Result(property = "content", column = "content"),
            @Result(property = "userid", column = "userid"),
            @Result(property = "goodsid", column = "goodsid"),
            @Result(property = "user", column = "userid", many = @Many(select = "com.sht.deal.Mapper.UserMapper.findById"))})
    List<MessageUtils> findComment(Integer goodsid, Integer userId);

    //查找nickname
    @Select("select nickname from user where id = #{id}")
    String findNickname(Integer id);

    @Select("select createtime, fans_id from fans where user_id = #{userId}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "createtime", column = "createtime"),
            @Result(property = "userid", column = "fans_id"),
            @Result(property = "user", column = "fans_id", many = @Many(select = "com.sht.deal.Mapper.UserMapper.findById"))})
    List<MessageUtils> findFans(Integer userId);

    @Select({"select type, typeid, createtime, userid from love where type_user_id = #{userId}"})
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "type", column = "type"),
            @Result(property = "typeid", column = "typeid"),
            @Result(property = "createtime", column = "createtime"),
            @Result(property = "userid", column = "userid"),
            @Result(property = "user", column = "userid", many = @Many(select = "com.sht.deal.Mapper.UserMapper.findById"))})
    List<Love> findLove(Integer userId);

    @Select("select content, goodsid from comment where commentid = #{typeid}")
    MessageUtils findCommentContent(Integer typeid);

    @Select("select content, goodsid from reply where id = #{typeid}")
    MessageUtils findReplyContent(Integer typeid);
}
