package com.sht.deal.utils;

import com.sht.deal.domain.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtUtils {
	private static final String SUBJECT = "shtDeal";
	private static final long EXPIRE = 1000*60*60*24*7; //7天
	private static final String APPSECRET = "e61451278486b545f860defcae13c19f";//"MD5", "sht666", "deal", 10

	public static String geneJsonWebToken(User user)
	{
		String token;
		if (user.getCode() == null){
			token = Jwts.builder().setSubject(SUBJECT)
					.claim("id", user.getId())
					.claim("username", user.getUsername())
					.claim("email", user.getEmail())
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + EXPIRE))  //设置到期时长
					.signWith(SignatureAlgorithm.HS256, APPSECRET)
					.compact();
		} else{
			token = Jwts.builder().setSubject(SUBJECT)
					.claim("id", user.getId())
					.claim("username", user.getUsername())
					.claim("email", user.getEmail())
					.setIssuedAt(new Date())
					.signWith(SignatureAlgorithm.HS256, APPSECRET)
					.compact();
		}
		return token;
	}

	public static Claims checkJWT(String token){
		try {
			return Jwts.parser().setSigningKey(APPSECRET).parseClaimsJws(token).getBody();
		}catch (ExpiredJwtException e){
			log.info("身份信息已过期");
			return null;
		}catch (Exception e){
			log.info("无效的身份信息");
			return null;
		}
	}

}
