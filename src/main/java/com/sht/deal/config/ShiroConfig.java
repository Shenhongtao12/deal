package com.sht.deal.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {

	@Bean
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		//设置安全管理器
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		Map<String, String> filerMap = new LinkedHashMap<String, String>();
		//开放的接口
		filerMap.put("/api/user/save", "anon");
		filerMap.put("/api/user/login", "anon");
		filerMap.put("/api/goods/findByPage", "anon");
		filerMap.put("/api/goods/findById", "anon");
		filerMap.put("/api/goods/findByLike", "anon");
		filerMap.put("/api/classify1/findAll", "anon");
		filerMap.put("/api/user/loginAdmin", "anon");

		//需要权限的接口
		//filerMap.put("/api/user/*","roles[超级管理员]");
		filerMap.put("/api/user/saveAdmin", "perms[user:add]");
		filerMap.put("/api/user/delete", "perms[user:delete]");
		filerMap.put("/api/user/addRoleToUser", "perms[user:role]");
		filerMap.put("/api/user/findOtherRoles", "perms[user:role]");
		filerMap.put("/api/user/findRoleById", "perms[user:role]");
		filerMap.put("api/token/role/*", "perms[role:perm]");
		filerMap.put("api/token/permission/*", "perms[role:perm]");
		filerMap.put("/api/classify1/add", "perms[classify:change]");
		filerMap.put("/api/classify1/delete", "perms[classify:change]");
		filerMap.put("/api/classify1/update", "perms[classify:change]");
		filerMap.put("/api/classify2/*", "perms[classify:change]");


		shiroFilterFactoryBean.setLoginUrl("/api/user/authorError?code=1");


		shiroFilterFactoryBean.setUnauthorizedUrl("/api/user/authorError?code=2");


		shiroFilterFactoryBean.setFilterChainDefinitionMap(filerMap);
		return shiroFilterFactoryBean;
	}

	@Bean(name = {"securityManager"})
	public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

		securityManager.setRealm(userRealm);
		securityManager.setRememberMeManager(rememberMeManager());
		return securityManager;
	}

	@Bean(name = {"userRealm"})
	public UserRealm getRealm() { return new UserRealm(); }


	@Bean
	public SimpleCookie rememberMeCookie() {
		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");

		simpleCookie.setMaxAge(259200);
		return simpleCookie;
	}

	@Bean
	public CookieRememberMeManager rememberMeManager() {
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCookie(rememberMeCookie());

		cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
		return cookieRememberMeManager;
	}
}
