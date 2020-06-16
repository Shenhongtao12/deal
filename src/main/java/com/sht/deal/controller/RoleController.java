package com.sht.deal.controller;

import com.sht.deal.domain.Middle;
import com.sht.deal.domain.Permission;
import com.sht.deal.domain.Role;
import com.sht.deal.service.RoleService;
import com.sht.deal.utils.PageResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//1.解决跨域
@CrossOrigin
@RequestMapping({"api/token/role"})
@RestController
public class RoleController {
    @PostMapping({"save"})
    public ResponseEntity save(@RequestBody Role role) {
        return ResponseEntity.ok(this.roleService.save(role));
    }


    @Autowired
    private RoleService roleService;


    @GetMapping({"findByPage"})
    public ResponseEntity<PageResult<Role>> findByPage(@RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "rows", defaultValue = "8") Integer rows) {
        return ResponseEntity.ok(this.roleService.findByPage(page, rows));
    }


    @GetMapping({"findById"})
    public ResponseEntity<Role> findById(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.ok(this.roleService.findById(id));
    }


    @PostMapping({"addPermissionByRoleId"})
    public ResponseEntity addPermissionByRoleId(@RequestBody Middle middle) {
        return ResponseEntity.ok(this.roleService.addPermissionByRoleId(middle));
    }


    @GetMapping({"findOtherPermission"})
    public ResponseEntity<List<Permission>> findOtherPermission(@RequestParam(name = "roleId") Integer roleId) {
        return ResponseEntity.ok(this.roleService.findOtherPermission(roleId));
    }


    @DeleteMapping({"delete"})
    public ResponseEntity delete(@RequestParam(name = "roleIds") Integer[] roleIds) {
        return ResponseEntity.ok(this.roleService.delete(roleIds));
    }

    @DeleteMapping({"deletePermissionToRole"})
    public ResponseEntity deletePermissionToRole(@RequestParam(name = "roleId") Integer roleId,
    @RequestParam(name = "permissionIds") String permissionIds){
        return ResponseEntity.ok(this.roleService.deletePermissionToRole(roleId, permissionIds));
    }
}
