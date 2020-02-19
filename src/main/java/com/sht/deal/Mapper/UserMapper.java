package com.sht.deal.Mapper;

import com.sht.deal.domain.Role;
import com.sht.deal.domain.User;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {
    @Select({"select id, nickname, img from user where id = #{id}"})
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "img", column = "img")})
    User findById(int id);

    @Select("select * from user where email = #{email} and id != #{id}")
    List<User> checkEmail(int id, String email);

    @Select("select email from user where username = #{username}")
    String findEmailByName(String username);

    @Update("update user set img = #{img} where id = #{id}")
    int updateImg(Integer id, String img);

    @Insert("insert into user_role(user_id, role_id) values (#{userId}, #{roleId})")
    void addRoleToUser(int userId, int roleId);

    @Select("select * from role where id not in (select role_id from user_role where user_id=#{userId})")
    List<Role> findOtherRoles(int userId);

    @Delete("DELETE FROM `user_role` WHERE `user_id` = #{id}")
    void deleteRole(int id);
}
