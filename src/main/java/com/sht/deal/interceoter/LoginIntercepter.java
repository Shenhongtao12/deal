package com.sht.deal.interceoter;

import com.google.gson.Gson;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class LoginIntercepter implements HandlerInterceptor {

	private static final Gson gson = new Gson();

	/**
	 * 进入controller之前进行拦截
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String token = request.getHeader("token");
		if (token == null){
			token = request.getParameter("token");
		}
		if (token != null) {
			Claims claims = JwtUtils.checkJWT(token);
			if (claims != null) {
				Integer userId = (Integer) claims.get("id");
				String username = (String) claims.get("username");
				log.info("当前操作的用户：" + username);

				request.setAttribute("user_id", userId);
				request.setAttribute("username", username);

				return true;
			}
		}
		sendJsonMessage(response, JsonData.buildError("请登录！"));
		return false;
	}

	/**
	 * 响应数据给前端
	 * @param response
	 * @param obj
	 */
	public static void sendJsonMessage(HttpServletResponse response, Object obj) throws IOException {
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.print(gson.toJson(obj));
		writer.close();
		try {
			response.flushBuffer();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
