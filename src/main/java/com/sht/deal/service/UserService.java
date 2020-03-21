package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.UserMapper;
import com.sht.deal.domain.Middle;
import com.sht.deal.domain.Role;
import com.sht.deal.domain.User;
import com.sht.deal.exception.AllException;
import com.sht.deal.service.RoleService;
import com.sht.deal.utils.DateUtils;
import com.sht.deal.utils.EmailCodeUtils;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.JwtUtils;
import com.sht.deal.utils.PageResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    public void checkout(User user) throws Exception {
        if (user
                .getUsername().getBytes("UTF-8").length > 30 || user
                .getPassword().getBytes("UTF-8").length > 40 || user
                .getPhone().getBytes("UTF-8").length > 20) {
            throw new AllException(-1, "数据校验失败，请重新输入");
        }
    }

    public User findById(int id) {
        User user = this.userMapper.selectByPrimaryKey(id);
        if (user == null) {
            throw new AllException(-1, "没有此用户");
        }
        user.setPassword("******");
        return user;
    }

    public Map registerAndLogin(String email, String code) {
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
        Map result = new HashMap();
        String token1 = JwtUtils.geneJsonWebToken(user);
        result.put("code", 0);
        result.put("token", token1);
        result.put("data", user);
        return result;
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


    public Map login(User userParam) {
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
        Map fans = new HashMap();
        //我关注的数量
        int Num1 = fansService.countNum("fans", user.getId());
        //粉丝的数量
        int Num2 = fansService.countNum("attention", user.getId());
        fans.put("fans", Num2);
        fans.put("attention", Num1);
        String token1 = JwtUtils.geneJsonWebToken(user);
        Map result = new HashMap();
        result.put("code", 0);
        result.put("token", token1);
        result.put("data", user);
        result.put("fans",fans);
        return result;
    }


    public JsonData loginAdmin(User userParam) {
        User user = findByUsername(userParam.getUsername());


        if (!"管".equals(user.getSex())) {
            throw new AllException(-1, "非管理员身份，登录失败");
        }


        Subject subject = SecurityUtils.getSubject();

        subject.isAuthenticated();

        SimpleHash simpleHash = new SimpleHash("MD5", userParam.getPassword(), SALT, 10);
        UsernamePasswordToken token = new UsernamePasswordToken(userParam.getUsername(), simpleHash.toString());

        try {
            subject.login(token);
            String token1 = JwtUtils.geneJsonWebToken(user);
            return JsonData.buildSuccess(token, token1);
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
        for (Integer id : ids) {
            userMapper.deleteRole(id);
            userMapper.deleteByPrimaryKey(id);
        }
        return JsonData.buildSuccess("删除成功");
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
        return JsonData.buildSuccess(user1, "更新成功");
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
            deleteImg(path);
        }
        return JsonData.buildSuccess(findById(user.getId()), "更新成功");
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
        if (user.getImg() == null) {
            this.userMapper.updateByPrimaryKeySelective(user);
            return JsonData.buildSuccess(findById(user.getId()), "更新成功");
        }
        User user1 = findById(user.getId());
        if (user.getImg().equals(user1.getImg())) {
            this.userMapper.updateByPrimaryKeySelective(user);
            return JsonData.buildSuccess(findById(user.getId()), "更新成功");
        }
        String path = user1.getImg();
        if (path == null || path.length() == 0) {
            this.userMapper.updateByPrimaryKeySelective(user);
            return JsonData.buildSuccess(findById(user.getId()), "更新成功");
        }
        this.userMapper.updateByPrimaryKeySelective(user);

        if (deleteImg(path)) {
            return JsonData.buildSuccess(findById(user.getId()), "更新成功");
        }
        return JsonData.buildSuccess(findById(user.getId()), "更新成功，旧图片删除失败");
    }


    public boolean deleteImg(String path) {
        path = path.substring(25);
        String name2 = path.substring(0, path.indexOf("thumbnail"));
        String jpg = path.substring(path.lastIndexOf("."));
        name2 = name2 + jpg;
        File file = new File(path);
        File file2 = new File(name2);
        return (file.delete() && file2.delete());
    }


    public int messageGet(int id) {
        String s = redisTemplate.boundValueOps(String.valueOf(id)).get();
        if (s == null) {
            return 0;
        }
        return Integer.valueOf(s);
    }


    public JsonData sendRegisterEmailCode(String email, String flag) throws Exception {
        String REGISTER_SUBJECT = "Eurasia二手交易平台";

        if (!checkEmailRule(email)) {
            throw new AllException(-1, "邮箱格式错误");
        }

        if (flag.equals("")) {
            if (checkEmail(email) > 1) {
                throw new AllException(-1, "邮箱已被注册");
            }
        } else if (checkEmail(email) > 0) {
            throw new AllException(-1, "邮箱已被注册");
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
        criteria.andEqualTo("username", user.getUsername());
        user.setUsername(null);
        int i = this.userMapper.updateByExampleSelective(user, example);
        if (i == 0) {
            return JsonData.buildError("修改失败");
        }
        return JsonData.buildSuccess("修改成功");
    }

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
        String strings[] = middle.getRoleIds().split(",");
        for (String string : strings) {
            userMapper.addRoleToUser(middle.getUserId(), Integer.valueOf(string));
        }

        return JsonData.buildSuccess("添加成功！");
    }


    public List<Role> findOtherRoles(Integer userId) {
        return this.userMapper.findOtherRoles(userId);
    }
}
