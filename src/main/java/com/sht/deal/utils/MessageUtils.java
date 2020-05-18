
package com.sht.deal.utils;

import com.sht.deal.domain.User;
import lombok.Data;

@Data
public class MessageUtils implements Comparable<MessageUtils> {
	private String images;
	private String name;
	private Integer goodsid;
	private String createtime;
	private String content;
	private Integer userid;
	private User user;

	public int compareTo(MessageUtils o) {
		if (getCreatetime().compareTo(o.getCreatetime()) > 0)
			return -1;
		else if (getCreatetime().compareTo(o.getCreatetime()) == 0) {
			return 0;
		}
		return 1;
	}
}
