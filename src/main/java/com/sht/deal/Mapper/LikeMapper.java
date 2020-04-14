package com.sht.deal.Mapper;

import com.sht.deal.domain.Love;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface LikeMapper extends Mapper<Love> {
	@Update("UPDATE reply SET `number` = number + #{num} WHERE `id` = #{id}")
	void like(Integer id, Integer num);

	@Update("UPDATE comment SET `number` = number + #{num} WHERE `commentid` = #{id}")
	void like1(Integer id, Integer num);

	@Select("select id from love where type = #{type} and typeid = #{typeid} and userid = #{userid}")
	Object findLoveBy(String type, int typeid, int userid);

	@Select("select userid from comment where commentid = #{typeid}")
	int findUserIdByComment(int typeid);

	@Select("select userid from reply where id = #{typeid}")
	int findUserIdByReply(int typeid);
}
