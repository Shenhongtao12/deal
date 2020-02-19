package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.CollectMapper;
import com.sht.deal.Mapper.GoodsMapper;
import com.sht.deal.Mapper.UserMapper;
import com.sht.deal.domain.Collect;
import com.sht.deal.domain.Goods;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


@Service
@Transactional
public class CollectService {
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserMapper userMapper;

    public JsonData save(Collect collect) {
        Collect collects = this.collectMapper.findOne(collect.getUserId(), collect.getGoodsId());
        if (collects != null) {
            this.collectMapper.deleteByPrimaryKey(collects.getId());
        } else {
            int i = this.collectMapper.insertSelective(collect);
            if (i != 1) {
                return JsonData.buildError("收藏失败");
            }
        }
        return JsonData.buildSuccess("成功");
    }


    public JsonData delete(Integer[] ids) {
        for (Integer id : ids) {
            collectMapper.deleteByPrimaryKey(id);
        }
        return JsonData.buildSuccess("删除成功");
    }


    public PageResult<Collect> findByUser(Integer id, Integer page, Integer rows) {
        Example example = new Example(Collect.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", id);
        PageHelper.startPage(page, rows);
        Page<Collect> collectPage = (Page<Collect>) collectMapper.selectByExample(example);
        for (Collect collect : collectPage) {
            Goods goods = (Goods) this.goodsMapper.selectByPrimaryKey(collect.getGoodsId());
            goods.setCode(true);
            goods.setUser(this.userMapper.findById(goods.getUserid()));
            collect.setGoods(goods);
        }
        return new PageResult<>(collectPage.getTotal(), collectPage.getPages(), collectPage.getResult());
    }

    public boolean checkGoods(Integer userId, Integer goodsId) {
        Example example = new Example(Collect.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("goodsId", goodsId);
        Collect collect = collectMapper.selectOneByExample(example);

        return (collect != null);
    }
}
