package com.sht.deal.controller;
import com.sht.deal.domain.Middle;
import com.sht.deal.domain.Role;
import com.sht.deal.domain.User;
import com.sht.deal.service.UserService;
import com.sht.deal.utils.PageResult;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"api/user"})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping({"findById"})
    public User findById(Integer id) {
        return this.userService.findById(id);
    }

    @PostMapping({"save"})
    public ResponseEntity save(@RequestBody User user) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.save(user));
    }


    @PostMapping({"saveAdmin"})
    public ResponseEntity saveAdmin(@RequestBody User user) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.saveAdmin(user));
    }


    @GetMapping({"registerAndLogin"})
    public ResponseEntity registerAndLogin(@RequestParam(name = "email") String email, @RequestParam(name = "code") String code) {
        return ResponseEntity.ok(this.userService.registerAndLogin(email, code));
    }


    @PostMapping({"login"})
    public ResponseEntity<Map> login(@RequestBody User userParam) {
        return new ResponseEntity(this.userService.login(userParam), HttpStatus.CREATED);
    }


    @PostMapping({"loginAdmin"})
    public ResponseEntity<Map> loginAdmin(@RequestBody User userParam) {
        return new ResponseEntity(this.userService.loginAdmin(userParam), HttpStatus.CREATED);
    }


    @GetMapping({"findAll"})
    public ResponseEntity<PageResult<User>> findAll(@RequestParam(name = "admin", required = false) String admin, @RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "rows", defaultValue = "10") Integer rows) {
        PageResult<User> userPageResult = this.userService.findAll(admin, page, rows);
        return ResponseEntity.ok(userPageResult);
    }


    @PutMapping({"updateImg"})
    public ResponseEntity updateImg(@RequestBody User user) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.updateImg(user));
    }


    @PutMapping({"updateName"})
    public ResponseEntity updateName(@RequestBody User user) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.updateName(user));
    }


    @PutMapping({"update"})
    public ResponseEntity update(@RequestBody User user) throws Exception {
        return ResponseEntity.ok(this.userService.update(user));
    }


    @GetMapping({"getMessage"})
    public ResponseEntity getMessage(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.ok(this.userService.messageGet(id));
    }


    @GetMapping({"getEmailCode"})
    public ResponseEntity sendRegisterEmailCode(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "flag", defaultValue = "") String flag) throws Exception {
        return ResponseEntity.ok(this.userService.sendRegisterEmailCode(email, flag));
    }


    @GetMapping({"findEmailByName"})
    public ResponseEntity findEmailByName(@RequestParam("username") String username) {
        return ResponseEntity.ok(this.userService.findEmailByName(username));
    }


    @PostMapping({"changePassword"})
    public ResponseEntity changePassword(@RequestBody User user) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.changePassword(user));
    }


    @PostMapping({"addRoleToUser"})
    public ResponseEntity addRoleToUser(@RequestBody Middle middle) {
        return ResponseEntity.ok(this.userService.addRoleToUser(middle));
    }


    @GetMapping({"findOtherRoles"})
    public ResponseEntity<List<Role>> findOtherRoles(@RequestParam(name = "userId") Integer userId) {
        return ResponseEntity.ok(this.userService.findOtherRoles(userId));
    }


    @GetMapping({"findRoleById"})
    public ResponseEntity<User> findRoleById(@RequestParam(name = "id") Integer id) {
        return ResponseEntity.ok(this.userService.findRoleById(id));
    }


    @DeleteMapping({"delete"})
    public ResponseEntity delete(@RequestParam(name = "ids") Integer[] ids) {
        return ResponseEntity.ok(this.userService.delete(ids));
    }
}
