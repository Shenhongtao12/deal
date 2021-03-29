package com.sht.deal.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sht.deal.Mapper.PostMapper;
import com.sht.deal.domain.Post;
import com.sht.deal.utils.JsonData;
import com.sht.deal.utils.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author Aaron
 * @date 2020/5/17 - 19:11
 **/
@Service
public class PostService {

    @Autowired
    private PostMapper postRepository;

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    public JsonData save(Post post) {
        if (post.getUserId() == null){
            return JsonData.buildError("数据错误，未关联用户");
        }
        post.setViews(0);
        post.setCreateTime(new Date());
        postRepository.insertSelective(post);
        return JsonData.buildSuccess("成功");
    }
    //更新浏览量
    public void updateViews(Integer postId){
        Post post = postRepository.selectByPrimaryKey(postId);
        post.setViews(post.getViews() + 1);
        postRepository.insertSelective(post);
    }

    //暂时不允许更新
    public JsonData update(Post post) {
        Post result = postRepository.selectByPrimaryKey(post.getId());
        if (result == null){
            return JsonData.buildError("数据错误，不存在该postId");
        }
        postRepository.insertSelective(post);
        return JsonData.buildSuccess("更新成功");
    }

    public JsonData delete(Integer id) {
        postRepository.deleteByPrimaryKey(id);
        return JsonData.buildSuccess("删除成功");
    }

    public Post findById(Integer postId, Integer userId) {
        Post post = postRepository.selectByPrimaryKey(postId);
        //List<Comment> commentList = commentService.findByPostId(postId, userId, "createTime");
        //post.setCommentList(commentList);
        post.setUser(userService.findById(post.getUserId()));
        //post.setCommentNum(commentList.size());
        return post;
    }

    //根据userId查找post
    public List<Post> findByUserId(Integer userId){
        Example example = new Example(Post.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        return postRepository.selectByExample(example);
    }

    public PageResult<Post> findByClassify(String searchName, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Post.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(searchName)) {
            criteria.andLike("title",  "%" + searchName + "%");
        }
        example.setOrderByClause("createTime");

        Page<Post> postPage = (Page<Post>) postRepository.selectAll();
        if (postPage.getResult().size() > 0){
            for (Post post : postPage) {
                post.setUser(userService.findById(post.getUserId()));
                //post.setCommentNum(commentService.countByPostId(post.getId()));
            }
        }
        return new PageResult<>(postPage.getTotal(), postPage.getPages(), postPage.getResult());
    }

    /*public JsonData findByFansUserId(Integer userId) {
        List<Post> postList = new ArrayList<>();
        List<Fans> byFansId = fansService.findByFansId(userId);
        for (Fans fans : byFansId) {
            List<Post> byUserId = postRepository.findByUserId(fans.getUserId());
            for (Post post : byUserId) {
                post.setUser(userService.findUserById(post.getUserId()));
                post.setCommentNum(commentService.countByPostId(post.getId()));
            }
            postList.addAll(byUserId);
        }
        Collections.sort(postList);
        return JsonData.buildSuccess(postList,"未完成");
    }*/
}
