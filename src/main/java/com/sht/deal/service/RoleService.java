package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.RoleMapper;
import com.sht.deal.domain.Middle;
import com.sht.deal.domain.Permission;
import com.sht.deal.domain.Role;
import com.sht.deal.service.PermissionService;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class RoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionService permissionService;

    public JsonData save(Role role) {
        int i = this.roleMapper.insert(role);
        if (i != 1) {
            return JsonData.buildError("添加失败");
        }
        return JsonData.buildSuccess("添加成功");
    }

    public PageResult<Role> findByPage(int page, int rows) {
        PageHelper.startPage(page, rows);
        Page<Role> rolePage = (Page<Role>) this.roleMapper.selectAll();
        return new PageResult<>(rolePage.getTotal(), rolePage.getPages(), rolePage.getResult());
    }


    public List<Role> findRoleByUserId(Integer id) {
        return this.roleMapper.findRoleByUserId(id);
    }


    public Role findById(Integer id) {
        Role role = roleMapper.selectByPrimaryKey(id);
        role.setPermissionList(this.permissionService.findPermissionByRoleId(id));
        return role;
    }


    public JsonData addPermissionByRoleId(Middle middle) {
        String strings[] = middle.getPermissionIds().split(",");
        for (String string : strings) {
            roleMapper.addPermissionByRoleId(middle.getRoleId(), Integer.valueOf(string));
        }
        return JsonData.buildSuccess("添加成功");
    }


    public List<Permission> findOtherPermission(int roleId) {
        return this.roleMapper.findOtherPermission(roleId);
    }

    public JsonData delete(Integer[] roleIds) {
        for (Integer roleId : roleIds) {
            roleMapper.deleteRolePermission(roleId);
            roleMapper.deleteRoleUser(roleId);
            roleMapper.deleteByPrimaryKey(roleId);
        }
        return JsonData.buildSuccess("删除成功");
    }

    public JsonData deletePermissionToRole(Integer roleId, Integer perId) {
        int i = roleMapper.deletePermissionToRole(roleId, perId);
        return JsonData.buildSuccess("成功");
    }
}
