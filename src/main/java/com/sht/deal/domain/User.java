package com.sht.deal.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;


/**
 * @JsonInclude(JsonInclude.Include.NON_NULL)
 * 将该标记放在属性上，如果该属性为NULL则不参与序列化
 * 如果放在类上边,那对这个类的全部属性起作用
 * Include.Include.ALWAYS 默认
 * Include.NON_DEFAULT 属性为默认值不序列化
 * Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
 * Include.NON_NULL 属性为NULL 不序列化
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User
{
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String username;
	private String email;
	private String password;
	private String nickname;
	private String openId;
	private String intro;  //介绍
	private String phone;
	private String sex;
	private String img;
	private String flag; //判断是否修改过用户名，只能修改一次   //注册的时候携带改参数flag="注册"

	@Transient
	private String code;//邮箱验证码
	@Transient
	private List<Role> roleList;


}
