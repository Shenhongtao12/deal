package com.sht.deal.controller;
import com.alibaba.fastjson.JSON;
import com.qq.connect.oauth.Oauth;
import com.sht.deal.config.QQConfig;
import com.sht.deal.domain.Middle;
import com.sht.deal.domain.Role;
import com.sht.deal.domain.User;
import com.sht.deal.exception.AllException;
import com.sht.deal.service.UserService;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.JwtUtils;
import com.sht.deal.utils.PageResult;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({"api/user"})
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 跳转到qq授权网页
     * @return
     */
    @GetMapping("/qqLogin")
    public void qqLogin(HttpSession session, HttpServletResponse response ) {
        /**
         * 防止请求受到攻击
         */
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        session.setAttribute("state", uuid);

        String url = QQConfig.GETQQPAGE+"?" +
                "response_type=code" +
                "&client_id=" + QQConfig.APPID +
                "&redirect_uri=" + URLEncoder.encode(QQConfig.BACKURL) +
                "&state=" + uuid;
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new AllException(-1, "跳转失败，请重试");
        }
    }


    @GetMapping("/to_qq")
    public void toLogin(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        try {
            String url = new Oauth().getAuthorizeURL(request);
            System.out.println("----------------qq登录URL-------------- " + url);
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * qq回调地址
     * @param request
     * @return
     */
    @GetMapping("/qqCallBack")
    public String callback(HttpServletRequest request, ModelMap modelMap) {
        //ModelMap modelMap = new ModelMap();
        HttpSession session = request.getSession();
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String uuid = (String) session.getAttribute("state");
        if (uuid != null) {
            if (!uuid.equals(state)) {
                System.out.println("TOKEN错误, 防止CSRF攻击, 业务异常处理......");
                return null;
            }
        }
        User qqUser = null;
        try {
            qqUser =  userService.saveQQUser(code);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("user", qqUser);
        Map fans = userService.findFansAndAttention(qqUser.getId());
        map2.put("fans", fans);
        map2.put("token", JwtUtils.geneJsonWebToken(qqUser));
        //modelMap.addAttribute("domain","used.eurasia.plus");

        map.put("code", 0);
        map.put("data", map2);
        modelMap.addAttribute("user", JSON.toJSON(map));
        return "qq";
    }


    @GetMapping({"findById"})
    public ResponseEntity findById(@RequestParam(name = "id") Integer id) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", userService.findById(id));
        map.put("fans", userService.findFansAndAttention(id));
        return ResponseEntity.ok(JsonData.buildSuccess(map,""));
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


    //已弃用
    //@GetMapping({"findEmailByName"})
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
