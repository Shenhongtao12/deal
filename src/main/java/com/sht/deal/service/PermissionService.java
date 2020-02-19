package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.PermissionMapper;
import com.sht.deal.domain.Permission;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    public List<Permission> findPermissionByRoleId(int id) {
        return this.permissionMapper.findPermissionByRoleId(id);
    }


    public JsonData save(Permission permission) {
        int i = this.permissionMapper.insert(permission);
        if (i != 1) {
            return JsonData.buildError("添加失败");
        }
        return JsonData.buildSuccess("添加成功");
    }

    public PageResult<Permission> findByPage(int page, int rows) {
        PageHelper.startPage(page, rows);
        Page<Permission> permissionPage = (Page<Permission>) this.permissionMapper.selectAll();
        return new PageResult<>(permissionPage.getTotal(), permissionPage.getPages(), permissionPage.getResult());
    }

    public JsonData delete(Integer[] permissionIds) {
        for (Integer permissionId : permissionIds) {
            permissionMapper.deleteRolePermission(permissionId);
            permissionMapper.deleteByPrimaryKey(permissionId);
        }
        return JsonData.buildSuccess("成功");
    }
}
