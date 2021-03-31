package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.PartTimeMapper;
import com.sht.deal.domain.PartTime;
import com.sht.deal.domain.User;
import com.sht.deal.exception.AllException;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author Aaron
 * @date 2021/3/28 12:47
 */
@Service
public class PartTimeService {

    @Autowired
    private PartTimeMapper partTimeMapper;
    @Autowired
    private UserService userService;

    public JsonData add(PartTime partTime) throws Exception {
        partTime.setCreateTime(new Date());
        partTime.setStatus(1);
        int i = this.partTimeMapper.insertSelective(partTime);
        if (i != 1) {
            throw new AllException(-1, "添加失败");
        }
        return JsonData.buildSuccess();
    }

    public PageResult<PartTime> findByPage(String name, Integer userId, int page, int rows) {
        Example example = new Example(PartTime.class);
        Example.Criteria criteria = example.createCriteria();
        if (name != null) {
            criteria.andLike("name", "%" + name + "%");
        }

        if (userId != null) {
            criteria.andEqualTo("userid", userId);
        }

        if (userId == null) {
            criteria.andEqualTo("status", 0);
        }
        PageHelper.startPage(page, rows);
        List<PartTime> buyList = this.partTimeMapper.selectByExample(example);
        if(buyList.size() > 0 && userId == null) {
            for (PartTime partTime : buyList) {
                User user = this.userService.findById(partTime.getUserid());
                partTime.setUser(user);
            }
        }
        Page<PartTime> buyPage = (Page<PartTime>)buyList;
        return new PageResult<>(buyPage.getTotal(), buyPage.getPages(), buyPage.getResult());
    }

    public JsonData delete(int id) {
        int i = this.partTimeMapper.deleteByPrimaryKey(id);
        return JsonData.buildSuccess(i, "删除成功");
    }

    public JsonData update(@RequestBody PartTime partTime){
        int i = partTimeMapper.updateByPrimaryKeySelective(partTime);
        if (i != 1){
            throw new AllException(-1, "更新失败");
        }
        return JsonData.buildSuccess(partTime,"更新成功");
    }

    public JsonData findById(Integer id) {
        if (!partTimeMapper.existsWithPrimaryKey(id)) {
            return JsonData.buildError("不存在该id");
        }
        PartTime partTime = partTimeMapper.selectByPrimaryKey(id);
        partTime.setUser(userService.findById(partTime.getUserid()));
        return JsonData.buildSuccess(partTime, "success");
    }
}
