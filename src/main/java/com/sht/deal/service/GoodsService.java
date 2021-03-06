package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.GoodsMapper;
import com.sht.deal.Mapper.ReplyMapper;
import com.sht.deal.Mapper.UserMapper;
import com.sht.deal.domain.Comment;
import com.sht.deal.domain.Goods;
import com.sht.deal.domain.Reply;
import com.sht.deal.domain.User;
import com.sht.deal.exception.AllException;
import com.sht.deal.utils.DateUtils;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
//@Transactional
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CollectService collectService;

    @Autowired
    private UploadService uploadService;

    public Map add(Goods goods) throws Exception {
        Map result = new HashMap();
        checkout(goods);
        if (goods.getClassify2_id() == null || goods.getUserid() == null) {
            throw new AllException(-1, "请选择分类");
        }
        // 给post设置为不展示
        if(StringUtils.equals("2", goods.getType())) {
            goods.setState(1);
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
        if (goods.getName().getBytes("UTF-8").length > 200) {
            throw new AllException(-1, "商品标题过长");
        }
        if (goods.getIntro().getBytes("UTF-8").length > 500) {
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

    public PageResult<Goods> findByPage(Integer id, String type, Integer classify1, String orderBy, Integer userid, String goodsName, int page, int rows) {
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        if (userid == null) {
            criteria.andEqualTo("state", 0);
        }
        if (type != null) {
            criteria.andEqualTo("type", type);
        }
        if (id != null) {
            criteria.andEqualTo("classify2_id", id);
        }
        if (goodsName != null){
            criteria.andLike("name", "%" + goodsName + "%");
        }

        if (userid != null) {
            criteria.andEqualTo("userid", userid);
        }
        //拼接一级分类下的二级分类id
        Example.Criteria c = example.createCriteria();
        if (classify1 != null) {
            int[] idArray = goodsMapper.findIdsByClassify1(classify1);
            for (int i = 0; i < idArray.length; i++) {
                c.orEqualTo("classify2_id", idArray[i]);

            }
            example.and(c);
        }
        //排序
        if (orderBy.length() > 0) {
            example.setOrderByClause(orderBy);
        }
        PageHelper.startPage(page, rows);
        Page<Goods> goodsPage = (Page<Goods>) this.goodsMapper.selectByExample(example);
        if (goodsPage.getTotal() > 0 && userid == null) {
            for (Goods goods : goodsPage) {
                goods.setUser(this.userMapper.findById(goods.getUserid()));
            }
        }

        return new PageResult<>(goodsPage.getTotal(), goodsPage.getPages(), goodsPage.getResult());
    }


    public JsonData deleteGoods(int id) {
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        if (goods == null){
            return JsonData.buildSuccess("已删除该商品");
        }
        String[] array = goods.getImages().split(",");
        if(array.length > 0){
            for (String image : array) {
                uploadService.deleteImage(image);
            }
        }
        //删除回复
        Example example = new Example(Reply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsid", id);
        replyMapper.deleteByExample(example);
        //删除留言
        commentService.deleteByGoodsId(id);
        //删除收藏表中的数据
        collectService.deleteByGoodsId(id);
        //删除商品
        int i = goodsMapper.deleteByPrimaryKey(id);
        if (i != 1) {
            throw new AllException(-1, "删除商品失败");
        }
        return JsonData.buildSuccess("删除成功");
    }


    public int findUserIdByGoodsId(int id) {
        return this.goodsMapper.findUserIdByGoodsId(id);
    }
}
