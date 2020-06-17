package com.sht.deal.service;

import com.sht.deal.Mapper.Classify2Mapper;
import com.sht.deal.domain.Classify1;
import com.sht.deal.domain.Classify2;
import com.sht.deal.exception.AllException;
import com.sht.deal.service.GoodsService;

import java.util.List;
import java.util.Objects;

import com.sht.deal.utils.JsonData;
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
    private UploadService uploadService;

    public JsonData add(Classify2 classify2) {
        int i = this.classify2Mapper.insertSelective(classify2);
        if (i != 1){
            return JsonData.buildError("失败");
        }
        return JsonData.buildSuccess("成功");
    }


    public JsonData delete(int id) {
        Classify2 classify2 = classify2Mapper.selectByPrimaryKey(id);
        try {
            this.classify2Mapper.deleteByPrimaryKey(id);
            this.uploadService.deleteImage(classify2.getImage());
            return JsonData.buildSuccess("删除成功");
        }catch (Exception e){
            return JsonData.buildError("删除失败,分类: " + classify2.getName() +",尚关联的有商品");
        }

    }

    public JsonData update(Classify2 classify2) {

        if (classify2.getImage() != null) {
            Classify2 classify = classify2Mapper.selectByPrimaryKey(classify2.getId());
            if (!classify2.getImage().equals(classify.getImage())){
                this.uploadService.deleteImage(classify.getImage());
            }
        }
        int i = this.classify2Mapper.updateByPrimaryKeySelective(classify2);
        if (i != 1){
            return JsonData.buildError("更新失败");
        }
        return JsonData.buildSuccess("更新成功");
    }


    public List<Classify2> findAll(int id) {
        Example example = new Example(Classify2.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("classify1id", id);
        return this.classify2Mapper.selectByExample(example);
    }
}
