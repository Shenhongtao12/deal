package com.sht.deal.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 帖子
 * @author Aaron
 * @date 2020/5/17 - 18:42
 **/
@Data
@Entity
@Table(name = "post")
public class Post implements Serializable,  Comparable<Post> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;
    @Column(name = "images_url")
    private String imagesUrl;
    private Date createTime;
    private Integer userId;
    private Integer views;
    /**
     * 留言的数量
     */
    /*@Transient
    private Integer commentNum;*/
    @Transient
    private User user;
    /*@Transient
    private List<Comment> commentList;*/

    @Override
    public int compareTo(Post o) {
        if (getCreateTime().compareTo(o.getCreateTime()) > 0) {
            return -1;
        } else if (getCreateTime().compareTo(o.getCreateTime()) == 0) {
            return 0;
        }
        return 1;
    }

}
