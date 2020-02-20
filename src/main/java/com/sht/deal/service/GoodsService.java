package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.GoodsMapper;
import com.sht.deal.Mapper.UserMapper;
import com.sht.deal.domain.Comment;
import com.sht.deal.domain.Goods;
import com.sht.deal.domain.Reply;
import com.sht.deal.domain.User;
import com.sht.deal.exception.AllException;
import com.sht.deal.service.CollectService;
import com.sht.deal.service.CommentService;
import com.sht.deal.service.UserService;
import com.sht.deal.utils.DateUtils;
import com.sht.deal.utils.PageResult;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


@Service
@Transactional
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CollectService collectService;

    public Map add(Goods goods) throws Exception {
        Map result = new HashMap();
        checkout(goods);
        if (goods.getClassify2_id() == null || goods.getUserid() == null) {
            throw new AllException(-1, "请选择分类");
        }

        goods.setCreate_time(DateUtils.dateByString());
        int i = this.goodsMapper.insertSelective(goods);
        if (i != 1) {
            throw new AllException(-1, "添加失败");
        }

        result.put("code", 0);
        result.put("msg", "添加成功");
        return result;
    }


    public void checkout(Goods goods) throws Exception {
        if (goods.getName().getBytes("UTF-8").length > 50) {
            throw new AllException(-1, "商品标题过长");
        }
        if (goods.getIntro().getBytes("UTF-8").length > 200) {
            throw new AllException(-1, "介绍内容过长");
        }
        if (goods.getWeixin().getBytes("UTF-8").length > 30) {
            throw new AllException(-1, "微信数据过长");
        }
    }

    public Map update(Goods goods) throws Exception {
        Map result = new HashMap();


        goods.setCreate_time(DateUtils.dateByString());
        int i = this.goodsMapper.updateByPrimaryKeySelective(goods);
        if (i != 1) {
            throw new AllException(-1, "更新失败");
        }
        result.put("code", 0);
        result.put("msg", "修改成功");
        return result;
    }

    public Goods findById(Integer id, Integer userid) throws Exception {
        Goods goods = (Goods) this.goodsMapper.selectByPrimaryKey(id);

        User user = this.userService.findById(goods.getUserid());
        goods.setUser(user);
        List<Comment> commentList = commentService.findByGoodsId(id, userid);
        goods.setCommentList(commentList);
        goods.setCommentNum(commentList.size());
        goods.setCode(this.collectService.checkGoods(userid, id));
        return goods;
    }

    public PageResult<Goods> findByPage(Integer id, String orderBy, Integer userid, int page, int rows) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        if (userid == null) {
            criteria.andEqualTo("state", 0);
        }
        if (id != null) {
            criteria.andEqualTo("classify2_id", id);
        }
        if (orderBy.length() > 0) {
            example.setOrderByClause(orderBy);
        }
        if (userid != null) {
            criteria.andEqualTo("userid", userid);
        }
        Page<Goods> goodsPage = (Page<Goods>) this.goodsMapper.selectByExample(example);
        if (goodsPage.getTotal() < 1) {
            throw new AllException(-1, "该分类没有商品");
        }
        if (userid == null) {
            for (Goods goods : goodsPage) {
                goods.setUser(this.userMapper.findById(goods.getUserid()));
            }
        }

        return new PageResult<>(goodsPage.getTotal(), goodsPage.getPages(), goodsPage.getResult());
    }


    public Map deleteGoods(int id) {
        Map result = new HashMap();
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        String array[] = goods.getImages().split(",");
        for (String image : array) {
            deleteImage(image.substring(37));
        }
        int i = goodsMapper.deleteByPrimaryKey(id);
        if (i != 1) {
            throw new AllException(-1, "删除商品失败");
        } else {
            result.put("code", 0);
            result.put("msg", "删除成功");
            return result;
        }
    }


    public PageResult<Goods> findByLike(String goodsName, int page, int rows) {
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("name", "%" + goodsName + "%");
        PageHelper.startPage(page, rows);
        Page<Goods> pageInfo = (Page<Goods>) this.goodsMapper.selectByExample(example);
        for (Goods goods : pageInfo) {

            User user = this.userMapper.findById(goods.getUserid());
            goods.setUser(user);
        }
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getResult());
    }

    public String deleteImage(String name) {
        String resultInfo = null;

        String path = "/deal/goods/" + name;

        String name2 = name.substring(0, name.indexOf("thumbnail"));
        String jpg = name.substring(name.lastIndexOf("."));
        name2 = name2 + jpg;
        String path2 = "/deal/goods/" + name2;
        File file = new File(path);
        File file2 = new File(path2);
        if (file.exists()) {
            if (file.delete() || file2.delete()) {
                resultInfo = "删除成功";
            } else {
                resultInfo = "删除失败";
            }
        } else {
            resultInfo = "文件不存在";
        }
        return resultInfo;
    }


    public int findUserIdByGoodsId(int id) {
        return this.goodsMapper.findUserIdByGoodsId(id);
    }
}
