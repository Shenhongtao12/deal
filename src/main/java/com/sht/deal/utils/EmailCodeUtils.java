package com.sht.deal.utils;

import com.sht.deal.exception.AllException;
import org.apache.commons.mail.HtmlEmail;

public class EmailCodeUtils
{
	public static String getNumber() {
		String str = "0123456789";
		String code = "";
		for (int i = 0; i < 6; i++) {
			int index = (int)(Math.random() * str.length());
			code = code + str.charAt(index);
		}
		return code;
	}


	public static void sendEmailCode(String EMAIL_HOST_NAME, String EMAIL_FORM_MAIL, String EMAIL_FORM_NAME, String EMAIL_AUTHENTICATION_USERNAME, String EMAIL_AUTHENTICATION_PASSWORD, String receiverEmail, String subject, String msg) {
		try {
			HtmlEmail email = new HtmlEmail();
			email.setHostName(EMAIL_HOST_NAME);
			email.setSmtpPort(465);
			email.setCharset("utf-8");
			email.setFrom(EMAIL_FORM_MAIL, EMAIL_FORM_NAME);
			email.setAuthentication(EMAIL_AUTHENTICATION_USERNAME, EMAIL_AUTHENTICATION_PASSWORD);
			email.addTo(receiverEmail);
			email.setSubject(subject);
			email.setMsg(msg);
			email.setSSLOnConnect(true);
			email.setSslSmtpPort("465");
			email.send();
		} catch (Exception e) {
			throw new AllException(-1, "发送验证码失败！请重新发送");
		}
	}
}
