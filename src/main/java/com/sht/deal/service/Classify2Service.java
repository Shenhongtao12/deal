package com.sht.deal.service;

import com.sht.deal.Mapper.Classify2Mapper;
import com.sht.deal.domain.Classify2;
import com.sht.deal.service.GoodsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


@Service
@Transactional
public class Classify2Service {
    @Autowired
    private Classify2Mapper classify2Mapper;
    @Autowired
    private GoodsService goodsService;

    public int add(Classify2 classify2) {
        return this.classify2Mapper.insertSelective(classify2);
    }


    public int delete(int id) {
        Classify2 classify2 = classify2Mapper.selectByPrimaryKey(id);
        this.goodsService.deleteImage(classify2.getImage());
        return this.classify2Mapper.deleteByPrimaryKey(id);
    }

    public int update(Classify2 classify2) {
        if (classify2.getImage() != null) {
            Classify2 classify = classify2Mapper.selectByPrimaryKey(classify2.getId());
            this.goodsService.deleteImage(classify.getImage());
            return this.classify2Mapper.updateByPrimaryKeySelective(classify2);
        }
        return this.classify2Mapper.updateByPrimaryKeySelective(classify2);
    }


    public List<Classify2> findAll(int id) {
        Example example = new Example(Classify2.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("classify1id", id);
        return this.classify2Mapper.selectByExample(example);
    }
}
