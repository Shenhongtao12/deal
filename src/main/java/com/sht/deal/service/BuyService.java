package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.BuyMapper;
import com.sht.deal.Mapper.UserMapper;
import com.sht.deal.domain.Buy;
import com.sht.deal.domain.User;
import com.sht.deal.exception.AllException;
import com.sht.deal.utils.DateUtils;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
@Transactional
public class BuyService {
	private static final Logger log = LoggerFactory.getLogger(BuyService.class);

	@Autowired
	private BuyMapper buyMapper;
	@Autowired
	private UserMapper userService;

	public void checkout(Buy buy) throws Exception {
		if (buy.getTitle().getBytes("UTF-8").length > 50) {
			throw new AllException(-1, "标题过长");
		}
		if (buy.getIntro().getBytes("UTF-8").length > 200) {
			throw new AllException(-1, "介绍内容过长");
		}
		if (buy.getWeixin().getBytes("UTF-8").length > 30)
			throw new AllException(-1, "微信号过长");
	}

	public JsonData add(Buy buy) throws Exception {
		checkout(buy);
		buy.setCreate_time(DateUtils.dateByString());
		int i = this.buyMapper.insertSelective(buy);
		if (i != 1) {
			throw new AllException(-1, "添加失败");
		}
		return JsonData.buildSuccess();
	}

	public PageResult<Buy> findByPage(Integer userid, String buyName, int page, int rows) {
		log.info("搜索名称：" + buyName);
		Example example = new Example(Buy.class);
		Example.Criteria criteria = example.createCriteria();
		if (userid != null) {
			criteria.andEqualTo("userid", userid);
		}
		if (buyName != null) {
			criteria.andLike("title", "%" + buyName + "%");
		}
		example.setOrderByClause("create_time desc");
		PageHelper.startPage(page, rows);
		List<Buy> buyList = this.buyMapper.selectByExample(example);
		for (Buy buy : buyList) {
			User user = this.userService.findById(buy.getUserid());
			buy.setUser(user);
		}
		Page<Buy> buyPage = (Page<Buy>)buyList;
		return new PageResult<>(buyPage.getTotal(), buyPage.getPages(), buyPage.getResult());
	}

	public JsonData delete(int id) {
		int i = this.buyMapper.deleteByPrimaryKey(id);
		return JsonData.buildSuccess(i, "删除成功");
	}

	public JsonData update(Buy buy) throws Exception {
		checkout(buy);
		//设置更改时间
		buy.setCreate_time(DateUtils.dateByString());
		int i = buyMapper.updateByPrimaryKeySelective(buy);
		if (i != 1){
			throw new AllException(-1, "更新失败");
		}
		return JsonData.buildSuccess(buy,"更新成功");
	}

	public PageResult<Buy> findByLike(String buyTitle, Integer page, Integer rows) {
		Example example = new Example(Buy.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andLike("title", "%" + buyTitle + "%");
		PageHelper.startPage(page, rows);

		List<Buy> buyList = this.buyMapper.selectByExample(example);
		for (Buy buy : buyList) {
			buy.setUser(userService.findById(buy.getUserid()));
		}
		Page<Buy> buyPage = (Page<Buy>)buyList;
		return new PageResult<>(buyPage.getTotal(), buyPage.getPages(), buyPage.getResult());
	}
}
