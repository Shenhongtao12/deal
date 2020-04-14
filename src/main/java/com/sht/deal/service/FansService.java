package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.FansMapper;
import com.sht.deal.Mapper.UserMapper;
import com.sht.deal.domain.Fans;
import com.sht.deal.service.FansService;
import com.sht.deal.utils.DateUtils;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
@Transactional
public class FansService {
    @Autowired
    private FansMapper fansMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    //既可以收藏也可以取消收藏
    public JsonData save(Fans fans) {
        Fans fans1 = this.fansMapper.findOne(fans.getUserId(), fans.getFansId());
        if (fans1 != null) {
            this.fansMapper.deleteByPrimaryKey(fans1.getId());
            if (redisTemplate.hasKey("fans-" + fans.getUserId()) && redisTemplate.opsForSet().isMember("fans-" + fans.getUserId(), String.valueOf(fans.getFansId()))){
                redisTemplate.opsForSet().remove("fans-" + fans.getUserId(), String.valueOf(fans.getFansId()));
            }
            return JsonData.buildSuccess("取消收藏成功");
        } else {
            fans.setCreatetime(DateUtils.dateByString());
            int i = this.fansMapper.insertSelective(fans);
            redisTemplate.opsForSet().add("fans-" + fans.getUserId(), String.valueOf(fans.getFansId()));
            if (i != 1) {
                return JsonData.buildError("失败");
            }
        }
        return JsonData.buildSuccess("收藏成功");
    }

    public JsonData delete(Integer id) {
        this.fansMapper.deleteByPrimaryKey(id);
        return JsonData.buildSuccess("取消成功");
    }

    //userId：我的粉丝     fansId：我的关注
    public PageResult<Fans> findFansToUser(Integer userId, Integer fansId, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fansId", fansId);
        Page<Fans> fansPage = (Page<Fans>) this.fansMapper.selectByExample(example);
        for (Fans fans : fansPage) {
            if (userId != null) {
                fans.setUser(this.userService.findById(fans.getFansId()));
                continue;
            }
            fans.setUser(this.userService.findById(fans.getUserId()));
        }

        return new PageResult<>(fansPage.getTotal(), fansPage.getPages(), fansPage.getResult());
    }

    public boolean checkFans(Integer userId, Integer toUserId) {
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", toUserId);
        criteria.andEqualTo("fansId", userId);
        List<Fans> fansList = this.fansMapper.selectByExample(example);
        return (fansList.size() > 0);
    }

    public Integer countNum(String type, Integer id){
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        if ("fans".equals(type)) {
            criteria.andEqualTo("fansId", id);
        }else {
            criteria.andEqualTo("userId", id);
        }
        return fansMapper.selectCountByExample(example);
    }
}
