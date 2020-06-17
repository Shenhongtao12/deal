package com.sht.deal.Mapper;

import com.sht.deal.domain.Permission;
import com.sht.deal.domain.Role;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface RoleMapper extends Mapper<Role> {
	@Select({"select * from role where id in(select role_id from user_role where user_id = #{id})"})
	@Results({
			@Result(id = true, property = "id", column = "id"),
			@Result(property = "roleName", column = "role_name"),
			@Result(property = "roleIntro", column = "role_intro"),
			@Result(property = "permissionList", column = "id", javaType = List.class, many = @Many(select = "com.sht.deal.Mapper.PermissionMapper.findPermissionByRoleId"))})
	List<Role> findRoleByUserId(int id);

	@Insert({"insert into role_permission(role_id, permission_id) values (#{roleId}, #{id}) "})
	void addPermissionByRoleId(int roleId, int id);

	@Select({"select * from permission where id not in(select permission_id from role_permission where role_id = #{roleId})"})
	List<Permission> findOtherPermission(int roleId);

	@Delete({"delete from role_permission where role_id = #{roleId}"})
	void deleteRolePermission(int roleId);

	@Delete({"delete from user_role where role_id = #{roleId}"})
	void deleteRoleUser(int roleId);

	@Delete("DELETE FROM `role_permission` WHERE `role_id` = #{roleId} and permission_id = #{perId}")
    void deletePermissionToRole(Integer roleId, Integer perId);
}
