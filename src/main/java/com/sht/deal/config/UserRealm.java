package com.sht.deal.config;

import com.sht.deal.domain.Permission;
import com.sht.deal.domain.Role;
import com.sht.deal.domain.User;
import com.sht.deal.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;


public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        Subject subject = SecurityUtils.getSubject();

        User user = (User) subject.getPrincipal();

        User dbUser = this.userService.findRoleById(user.getId());

        if (null != dbUser.getRoleList()) {
            for (Role role : dbUser.getRoleList()) {
                info.addRole(role.getRoleName());
                for (Permission permission : role.getPermissionList()) {
                    info.addStringPermission(permission.getUrl());
                }
            }
        }
        return info;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        User user = this.userService.findByUsername(token.getUsername());

        if (user == null) {
            return null;
        }

        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }
}
