package com.sht.deal.controller;

import com.sht.deal.domain.Permission;
import com.sht.deal.service.PermissionService;
import com.sht.deal.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//1.解决跨域
@CrossOrigin
@RestController
@RequestMapping({"api/token/permission"})
public class PermissionController {
    @Autowired
    private PermissionService permissionService;
    @PostMapping({"save"})
    public ResponseEntity save(@RequestBody Permission permission) {
        return ResponseEntity.ok(this.permissionService.save(permission));
    }

    @GetMapping({"findByPage"})
    public ResponseEntity<PageResult<Permission>> findByPage(@RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "rows", defaultValue = "8") Integer rows) {
        return ResponseEntity.ok(this.permissionService.findByPage(page, rows));
    }


    @DeleteMapping({"delete"})
    public ResponseEntity delete(@RequestParam(name = "permissionIds") Integer[] permissionIds) {
        return ResponseEntity.ok(this.permissionService.delete(permissionIds));
    }
}
