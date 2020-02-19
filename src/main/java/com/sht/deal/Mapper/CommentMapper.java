package com.sht.deal.Mapper;

import com.sht.deal.domain.Comment;

import java.util.List;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface CommentMapper extends Mapper<Comment> {
    @Select("select * from comment where goodsid = #{id}")
    @Results({
            @Result(id = true, property = "commentid", column = "commentid"),
            @Result(property = "createtime", column = "createtime"),
            @Result(property = "content", column = "content"),
            @Result(property = "number", column = "number"),
            @Result(property = "userid", column = "userid"),
            @Result(property = "goodsid", column = "goodsid"),
            @Result(property = "leaf", column = "leaf"),
            @Result(property = "user", column = "userid", many = @Many(select = "com.sht.deal.Mapper.UserMapper.findById"))})
    List<Comment> findByGoodsId(int id);
}
