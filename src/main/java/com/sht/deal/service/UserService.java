package com.sht.deal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.UserMapper;
import com.sht.deal.config.QQConfig;
import com.sht.deal.domain.Middle;
import com.sht.deal.domain.Role;
import com.sht.deal.domain.User;
import com.sht.deal.exception.AllException;
import com.sht.deal.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Transactional
public class UserService {
    @Value("${email.host-name}")
    public String EMAIL_HOST_NAME;
    @Value("${email.authentication.username}")
    public String EMAIL_AUTHENTICATION_USERNAME;
    @Value("${email.authentication.password}")
    public String EMAIL_AUTHENTICATION_PASSWORD;
    @Value("${email.charset}")
    public String EMAIL_CHARSET;
    @Value("${email.form.mail}")
    public String EMAIL_FORM_MAIL;
    @Value("${email.form.name}")
    public String EMAIL_FORM_NAME;
    private static final String SALT = "second-hand+sht666";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private FansService fansService;
    @Autowired
    private UploadService uploadService;

    public void checkout(User user) throws Exception {
        if (user
                .getUsername().getBytes("UTF-8").length > 30 || user
                .getPassword().getBytes("UTF-8").length > 40 || user
                .getPhone().getBytes("UTF-8").length > 20) {
            throw new AllException(-1, "数据校验失败，请重新输入");
        }
    }

    //qq回调
    public User saveQQUser(String code) {
        // 获取Access Token
        String access_url = QQConfig.GETACCESSTOKEN+"?grant_type=authorization_code" +
                "&client_id=" + QQConfig.APPID +
                "&client_secret=" + QQConfig.APPKEY +
                "&code=" + code +
                "&redirect_uri=" + QQConfig.BACKURL;
        String  access_res = HttpClientUtils.httpGet(access_url);
        String access_token = "";
        if (access_res.indexOf("access_token") >= 0) {
            String[] array = access_res.split("&");
            for (String str: array)
                if (str.indexOf("access_token") >= 0) {
                    access_token = str.substring(str.indexOf("=") + 1);
                    break;
                }
        }

        // 获取qq账户 openId
        String open_id_url = QQConfig.GETACCOUNTOPENID+"?access_token="+access_token;
        String open_id_res = HttpClientUtils.httpGet(open_id_url);
        int startIndex = open_id_res.indexOf("(");
        int endIndex = open_id_res.lastIndexOf(")");
        String open_id_res_str = open_id_res.substring(startIndex + 1, endIndex);
        JSONObject jsonObject = JSON.parseObject(open_id_res_str);
        String openId = jsonObject.getString("openid");

        // 获取账户qq信息
        String account_info_url = QQConfig.GETACCOUNTINFO+"?access_token="+access_token+
                "&oauth_consumer_key=" + QQConfig.APPID +
                "&openid=" + openId;
        String account_info_res = HttpClientUtils.httpGet(account_info_url);
        //System.out.println(account_info_res);
        JSONObject userJson = JSON.parseObject(account_info_res);
        User qqUser = new User();
        qqUser.setOpenId(openId);
        qqUser.setNickname(userJson.getString("nickname"));
        qqUser.setSex(userJson.getString("gender"));
        qqUser.setImg(userJson.getString("figureurl_qq_1"));

        // 判断openid在系统中是否存在
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", openId);
        User isExits = userMapper.selectOneByExample(example);
        if(isExits == null){
            // 第一次使用qq登录
            userMapper.insertSelective(qqUser);
            int id = qqUser.getId();
            return findById(id);
        }
        return isExits;
    }


    //根据id查用户
    public User findById(int id) {
        User user = this.userMapper.selectByPrimaryKey(id);
        if (user == null) {
            return new User();
        }
        user.setPassword("******");
        return user;
    }

    public JsonData registerAndLogin(String email, String code) {
        String emailCode = (String) this.redisTemplate.boundValueOps(email).get();
        if (StringUtils.isEmpty(emailCode)) {
            throw new AllException(-1, "验证码错误或者已过期，请重新发送");
        }
        if (!emailCode.equals(code.toUpperCase())) {
            throw new AllException(-1, "验证码错误");
        }
        User user = new User();
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if (checkEmail(email) >= 1) {
            criteria.andEqualTo("email", email);
            user = (User) this.userMapper.selectOneByExample(example);
        } else {
            user.setUsername(email);
            user.setEmail(email);
            user.setNickname(email);
            this.userMapper.insertSelective(user);
            user.setId(user.getId());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("token", JwtUtils.geneJsonWebToken(user));
        result.put("user", user);
        result.put("fans", findFansAndAttention(user.getId()));
        return JsonData.buildSuccess(result,"");
    }

    public JsonData saveAdmin(User user) {
        if (Objects.nonNull(isExist(user))) {
            throw new AllException(-1, String.valueOf(isExist(user)));
        }
        user.setSex("管");
        user.setNickname(user.getUsername());                                           //second-hand+sht666
        SimpleHash simpleHash = new SimpleHash("MD5", user.getPassword(), SALT, 10);
        user.setPassword(simpleHash.toString());
        int i = this.userMapper.insertSelective(user);
        if (i != 1) {
            return JsonData.buildError("添加失败");
        }
        return JsonData.buildSuccess("添加成功");
    }


    public JsonData save(User user) throws Exception {
        checkout(user);

        SimpleHash simpleHash = new SimpleHash("MD5", user.getPassword(), SALT, 10);
        user.setPassword(simpleHash.toString());
        if (Objects.nonNull(isExist(user))) {
            throw new AllException(-1, String.valueOf(isExist(user)));
        }
        String emailCode = (String) this.redisTemplate.boundValueOps(user.getEmail()).get();
        if (StringUtils.isEmpty(emailCode)) {
            throw new AllException(-1, "验证码错误或者已过期，请重新发送");
        }
        if (!emailCode.equals(user.getCode().toUpperCase())) {
            throw new AllException(-1, "验证码错误");
        }

        user.setNickname(user.getUsername());
        int i = this.userMapper.insertSelective(user);
        if (i == 0) {
            return JsonData.buildError("注册失败");
        }
        int id = user.getId();
        return JsonData.buildSuccess(id, "注册成功");
    }

    //获取关注数和粉丝数
    public Map<String, Object> findFansAndAttention(Integer UserId){
        Map<String, Object> fans = new HashMap<>();
        //我关注的数量
        int Num1 = fansService.countNum("fans", UserId);
        //粉丝的数量
        int Num2 = fansService.countNum("attention", UserId);
        fans.put("fans", Num2);
        fans.put("attention", Num1);
        return fans;
    }

    public Map<String, Object> login(User userParam) {
        Map<String, Object> map = new HashMap<>();
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.orEqualTo("username", userParam.getUsername());
        criteria.orEqualTo("email", userParam.getUsername());
        User user = (User) this.userMapper.selectOneByExample(example);
        SimpleHash simpleHash = new SimpleHash("MD5", userParam.getPassword(), SALT, 10);
        if (user == null || !user.getPassword().equals(simpleHash.toString())) {
            throw new AllException(-1, "用户名或密码错误");
        }
        //粉丝，关注数量
        Map<String, Object> result = new HashMap<>();
        result.put("token", JwtUtils.geneJsonWebToken(user));
        result.put("user", user);
        result.put("fans",findFansAndAttention(user.getId()));
        map.put("code", 0);
        map.put("data", result);
        return map;
    }


    public JsonData loginAdmin(User userParam) {
        User user = findByUsername(userParam.getUsername());

        if (user != null && !"管".equals(user.getSex())) {
            throw new AllException(-1, "非管理员身份，登录失败");
        }

        Subject subject = SecurityUtils.getSubject();

        subject.isAuthenticated();

        SimpleHash simpleHash = new SimpleHash("MD5", userParam.getPassword(), SALT, 10);
        UsernamePasswordToken token = new UsernamePasswordToken(userParam.getUsername(), simpleHash.toString());

        try {
            subject.login(token);
            String token1 = JwtUtils.geneJsonWebToken(user);
            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            map.put("token", token1);
            return JsonData.buildSuccess(map, "登录成功");
        } catch (UnknownAccountException e) {
            return JsonData.buildError("用户名错误");
        } catch (IncorrectCredentialsException e) {
            return JsonData.buildError("密码错误");
        }
    }

    public PageResult<User> findAll(String admin, int page, int rows) {
        Page<User> userPage;
        PageHelper.startPage(page, rows);
        if (admin != null) {
            Example example = new Example(User.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("sex", "管");
            userPage = (Page<User>) this.userMapper.selectByExample(example);
        } else {
            userPage = (Page<User>) this.userMapper.selectAll();
        }
        return new PageResult<>(userPage.getTotal(), userPage.getPages(), userPage.getResult());
    }


    public JsonData delete(Integer[] ids) {
        try {
            for (Integer id : ids) {
                userMapper.deleteRole(id);
                userMapper.deleteByPrimaryKey(id);
            }
            return JsonData.buildSuccess("删除成功");
        }catch (Exception e){
            return JsonData.buildError("删除失败，该用户不可删除");
        }

    }


    public JsonData updateName(User user) {
        User user1 = findById(user.getId());
        if ("false".equals(user1.getFlag())) {
            return JsonData.buildError("您已修改过用户名，不允许再次修改");
        }
        if (Objects.nonNull(isExist(user))) {
            throw new AllException(-1, String.valueOf(isExist(user)));
        }
        user.setFlag("false");
        this.userMapper.updateByPrimaryKeySelective(user);
        user1.setUsername(user.getUsername());
        user1.setFlag("false");
        Map<String, Object> map = new HashMap<>();
        map.put("user", user1);
        map.put("fans", findFansAndAttention(user.getId()));
        map.put("token", JwtUtils.geneJsonWebToken(user));
        return JsonData.buildSuccess(map, "更新成功");
    }

    public JsonData updateImg(User user) {
        User user1 = findById(user.getId());
        String path = user1.getImg();
        if (path == null || path.length() == 0) {
            this.userMapper.updateImg(user.getId(), user.getImg());
        } else {
            int i = this.userMapper.updateImg(user.getId(), user.getImg());
            if (i != 1) {
                throw new AllException(-1, "更新失败");
            }
            if (path.contains("/deal/user")){
                uploadService.deleteImage(path);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("user", findById(user.getId()));
        map.put("fans", findFansAndAttention(user.getId()));
        map.put("token", JwtUtils.geneJsonWebToken(user));
        return JsonData.buildSuccess(map, "更新成功");
    }


    public JsonData update(User user) {
        user.setPassword(null);

        if (!checkEmailRule(user.getEmail())) {
            throw new AllException(-1, "邮箱格式错误");
        }
        List<User> userList = this.userMapper.checkEmail(user.getId(), user.getEmail());
        if (userList.size() > 0) {
            throw new AllException(-1, "邮箱已被绑定");
        }
        Map fans = findFansAndAttention(user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("fans", fans);
        if (user.getImg() != null) {
            User user1 = findById(user.getId());
            if (user1.getImg().contains("/deal/user")){
                uploadService.deleteImage(user1.getImg());
            }
        }
        this.userMapper.updateByPrimaryKeySelective(user);
        map.put("user", findById(user.getId()));
        map.put("token", JwtUtils.geneJsonWebToken(user));
        map.put("fans", findFansAndAttention(user.getId()));
        return JsonData.buildSuccess(map, "更新成功");
    }


    public int messageGet(int id) {
        //获取回复数量
        String reply = redisTemplate.boundValueOps(String.valueOf(id)).get();
        //获取关注的数量
        Long fans = redisTemplate.opsForSet().size("fans-" + id);
        if (reply == null) {
            return fans.intValue();
        }
        return Integer.parseInt(reply) + fans.intValue();
    }


    //发送邮箱验证码
    public JsonData sendRegisterEmailCode(String email, String flag) throws Exception {
        String REGISTER_SUBJECT = "Eurasia二手交易平台";

        if (!checkEmailRule(email)) {
            throw new AllException(-1, "邮箱格式错误");
        }

        if ("注册".equals(flag)) { //注册
            if (checkEmail(email) > 0) {
                throw new AllException(-1, "邮箱已被注册");
            }
        } else {   //登录，修改密码
            if (checkEmail(email) > 1){
                throw new AllException(-1, "邮箱已被注册");
            } else if(checkEmail(email) == 0){  //没有注册的用户去修改密码发送验证码时执行
                throw new AllException(-1, "该邮箱还未注册");
            }
        }


        String s = "deal" + email;
        String num = (String) this.redisTemplate.boundValueOps(s).get();

        if ("8".equals(num) || StringUtils.length(num) > 8) {
            if ("8".equals(num)) {
                this.redisTemplate.boundValueOps(s).set(DateUtils.dateByString());
            }
            String str = (String) this.redisTemplate.boundValueOps(s).get();
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = sdf2.parse(str);
            Date d2 = new Date();
            long diff = 3600L - (d2.getTime() - d1.getTime()) / 1000L;
            if (diff < 60L) {
                throw new AllException(-1, "操作过于频繁，请" + diff + "秒以后再试");
            }
            throw new AllException(-1, "操作过于频繁，请" + (diff / 60L) + "分钟以后再试");
        }


        String emailCode = EmailCodeUtils.getNumber();
        String REGISTER_MSG = "<head><base target=\"_blank\" /><style type=\"text/css\">::-webkit-scrollbar{ display: none; }</style><style id=\"cloudAttachStyle\"type=\"text/css\">#divNeteaseBigAttach, #divNeteaseBigAttach_bak{display:none;}</style><style id=\"blockquoteStyle\" type=\"text/css\">blockquote{display:none;}</style><style type=\"text/css\">body{font-size:14px;font-family:arial,verdana,sans-serif;line-height:1.666;padding:0;margin:0;overflow:auto;white-space:normal;word-wrap:break-word;min-height:100px}td, input, button, select, body{font-family:Helvetica, 'Microsoft Yahei', verdana}pre {white-space:pre-wrap;white-space:-moz-pre-wrap;white-space:-pre-wrap;white-space:-o-pre-wrap;word-wrap:break-word;width:95%}th,td{font-family:arial,verdana,sans-serif;line-height:1.666}img{ border:0}header,footer,section,aside,article,nav,hgroup,figure,figcaption{display:block}blockquote{margin-right:0px}</style>\n</head><body tabindex=\"0\" role=\"listitem\"><table width=\"700\" border=\"0\" align=\"center\" cellspacing=\"0\" style=\"width:700px;\"><tbody><tr><td><div style=\"width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"700\" height=\"39\" style=\"font:12px Tahoma, Arial, 宋体;\"><tbody><tr><td width=\"210\"></td></tr></tbody></table></div><div style=\"width:680px;padding:0 10px;margin:0 auto;\"><div style=\"line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;\"><strong style=\"display:block;margin-bottom:15px;\">尊敬的用户：<span style=\"color:#f60;font-size: 16px;\"></span>您好！</strong><strong style=\"display:block;margin-bottom:15px;\">您正在进行<span style=\"color: red\">安全邮箱验证</span>操作，请在验证码输入框中输入：<span style=\"color:#f60;font-size: 24px\">" + emailCode + "</span>，以完成操作，有效时间5分钟。</strong></div><div style=\"margin-bottom:30px;\"><small style=\"display:block;margin-bottom:20px;font-size:12px;\"><p style=\"color:#747474;\">注意：此操作可能会修改您的密码、登录邮箱或绑定账号。如非本人操作，请及时登录并修改密码以保证帐户安全<br>（工作人员不会向你索取此验证码，请勿泄漏！)</p></small></div></div><div style=\"width:700px;margin:0 auto;\"><div style=\"padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;\"><p>此为系统邮件，请勿回复<br>请保管好您的邮箱，避免账号被他人盗用</p><p>Eurasia网络科技团队</p></div></div></td></tr></tbody></table></body>";


        EmailCodeUtils.sendEmailCode(this.EMAIL_HOST_NAME, this.EMAIL_FORM_MAIL, this.EMAIL_FORM_NAME, this.EMAIL_AUTHENTICATION_USERNAME, this.EMAIL_AUTHENTICATION_PASSWORD, email, REGISTER_SUBJECT, REGISTER_MSG);


        this.redisTemplate.boundValueOps(email).set(emailCode, 5L, TimeUnit.MINUTES);

        if (StringUtils.isEmpty(num)) {
            this.redisTemplate.boundValueOps(s).set("1", 1L, TimeUnit.HOURS);
        } else {
            this.redisTemplate.boundValueOps(s).increment(1L);
        }
        return JsonData.buildSuccess("发送成功");
    }


    private Object isExist(User user) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", user.getUsername());
        User currentUser = (User) this.userMapper.selectOneByExample(example);
        if (Objects.nonNull(currentUser) &&
                currentUser.getUsername().equals(user.getUsername())) return "用户名已存在";

        return null;
    }


    public int checkEmail(String email) {
        if (!checkEmailRule(email)) {
            throw new AllException(-1, "邮箱格式错误");
        }
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("email", email);
        List<User> userList = this.userMapper.selectByExample(example);

        return userList.size();
    }

    public Boolean checkEmailRule(String email) {
        if (email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)")) {
            return false;
        }
        return true;
    }


    public JsonData changePassword(User user) {
        String emailCode = (String) this.redisTemplate.boundValueOps(user.getEmail()).get();
        if (!user.getCode().toUpperCase().equals(emailCode)) {
            throw new AllException(-1, "验证码错误");
        }
        SimpleHash simpleHash = new SimpleHash("MD5", user.getPassword(), SALT, 10);
        user.setPassword(simpleHash.toString());
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("email", user.getEmail());
        user.setUsername(null);
        int i = this.userMapper.updateByExampleSelective(user, example);
        if (i == 0) {
            return JsonData.buildError("修改失败");
        }
        return JsonData.buildSuccess("修改成功");
    }

    //已弃用
    public String findEmailByName(String username) {
        String s = this.userMapper.findEmailByName(username);
        if (StringUtils.isEmpty(s)) {
            return "null";
        }
        return s;
    }

    public User findByUsername(String username) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        return (User) this.userMapper.selectOneByExample(example);
    }


    public User findRoleById(int id) {
        List<Role> roleList = this.roleService.findRoleByUserId(id);
        User user = (User) this.userMapper.selectByPrimaryKey(id);
        user.setRoleList(roleList);
        return user;
    }


    public JsonData addRoleToUser(Middle middle) {
        if (middle.getUserId() == 5){
            return JsonData.buildError("您无权操作该管理员");
        }
        String strings[] = middle.getRoleIds().split(",");
        for (String string : strings) {
            userMapper.addRoleToUser(middle.getUserId(), Integer.valueOf(string));
        }

        return JsonData.buildSuccess("添加成功！");
    }


    public List<Role> findOtherRoles(Integer userId) {
        return this.userMapper.findOtherRoles(userId);
    }

    public JsonData deleteRoleToUser(Integer userId, String roleIds) {
        if (userId == 5){
            return JsonData.buildError("您无权操作该管理员");
        }
        String strings[] = roleIds.split(",");
        for (String roleId : strings) {
            userMapper.deleteRoleToUser(userId, Integer.parseInt(roleId));
        }
        return JsonData.buildSuccess("成功");
    }
}
