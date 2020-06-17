package com.sht.deal.service;

import com.sht.deal.Mapper.Classify1Mapper;
import com.sht.deal.domain.Classify1;
import com.sht.deal.domain.Classify2;
import com.sht.deal.exception.AllException;
import com.sht.deal.service.Classify2Service;
import com.sht.deal.service.GoodsService;

import java.util.List;
import java.util.Objects;

import com.sht.deal.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


@Service
public class Classify1Service {
    @Autowired
    private Classify1Mapper classify1Mapper;
    @Autowired
    private Classify2Service classify2Service;
    @Autowired
    private UploadService uploadService;

    public JsonData add(Classify1 classify1) {
        if (Objects.nonNull(isExist(classify1))) {
            throw new AllException(-1, String.valueOf(isExist(classify1)));
        }
        int i = this.classify1Mapper.insertSelective(classify1);
        if (i != 1){
            return JsonData.buildError("失败");
        }
        return JsonData.buildSuccess("成功");
    }


    public Classify1 findById(Integer id) {
        return classify1Mapper.selectByPrimaryKey(id);
    }


    public JsonData delete(Integer id) {
        Classify1 classify1 = classify1Mapper.selectByPrimaryKey(id);
        try {
            this.classify1Mapper.deleteByPrimaryKey(id);
            this.uploadService.deleteImage(classify1.getImage());
            return JsonData.buildSuccess("成功");
        }catch (Exception e ){
            return JsonData.buildError("删除失败,分类: " + classify1.getName() +",尚关联的有二级分类");
        }
    }

    public JsonData update(Classify1 classify1) {
        if (classify1.getImage() != null) {
            Classify1 classify = classify1Mapper.selectByPrimaryKey(classify1.getId());
            if (!classify1.getImage().equals(classify.getImage())){
                this.uploadService.deleteImage(classify.getImage());
            }
        }
        int i = this.classify1Mapper.updateByPrimaryKeySelective(classify1);
        if (i != 1){
            return JsonData.buildError("更新失败");
        }
        return JsonData.buildSuccess("更新成功");
    }


    public List<Classify2> findChildById(int id) {
        return classify2Service.findAll(id);
    }


    public List<Classify1> findAll() {
        List<Classify1> classify1List = this.classify1Mapper.selectAll();
        for (Classify1 classify1 : classify1List) {
            List<Classify2> classify2List = classify2Service.findAll(classify1.getId());
            classify1.setClassify2List(classify2List);
        }
        return classify1List;
    }


    private Object isExist(Classify1 classify1) {
        Example example = new Example(Classify1.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", classify1.getName());
        Classify1 classify = (Classify1) this.classify1Mapper.selectOneByExample(example);
        if (Objects.nonNull(classify) &&
                classify.getName().equals(classify1.getName())){
            return "分类名已存在";
        }
        return null;
    }


    public List<Classify1> findClassify1() {
        return this.classify1Mapper.selectAll();
    }
}
