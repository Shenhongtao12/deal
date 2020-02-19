package com.sht.deal.Mapper;

import com.sht.deal.domain.Permission;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface PermissionMapper extends Mapper<Permission> {

	@Select("select * from permission where id in(select permission_id from role_permission where role_id =#{id})")
	List<Permission> findPermissionByRoleId(int id);

	@Delete("delete from role_permission where permission_id = #{permissionId}")
	void deleteRolePermission(int permissionId);
}
