package com.sht.deal.service;

import com.sht.deal.Mapper.LikeMapper;
import com.sht.deal.domain.Love;
import com.sht.deal.utils.DateUtils;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


@Service
@Transactional
public class LikeService {
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void save(Object type, Object state) {
        this.redisTemplate.boundHashOps("loveHash").put(type, state);
    }


    @Scheduled(cron = "0/10 * * * * ?")
    public void saveSql() {
        Set key = this.redisTemplate.boundHashOps("loveHash").keys();
        List value = this.redisTemplate.boundHashOps("loveHash").values();
        if (!key.isEmpty() && !value.isEmpty()) {
            int p = key.toString().length() - 1;
            String array0 = key.toString().substring(1, p);

            String[] array1 = array0.split(",");
            for (int i = 0; i < array1.length; i++) {

                this.redisTemplate.opsForHash().delete("loveHash", array1[i].trim());
            }
            for (int i = 0; i < value.size(); i++) {
                String[] num = array1[i].split(":");
                Example example = new Example(Love.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("type", num[0].trim());  //留言还是回复
                criteria.andEqualTo("typeid", Integer.valueOf(num[1].trim())); //留言id
                criteria.andEqualTo("userid", Integer.valueOf(num[2].trim())); //userid
                List<Love> loveList = this.likeMapper.selectByExample(example);
                if (value.get(i).equals("0")) {  //取消点赞
                    if (loveList.size() > 0) {   //并且数据库中存在记录的
                        this.likeMapper.deleteByExample(example);
                        //点赞数量-1
                        if (num[0].trim().equals("reply")) {
                            this.likeMapper.like(Integer.valueOf(num[1].trim()), -1);
                        } else {
                            this.likeMapper.like1(Integer.valueOf(num[1].trim()), -1);
                        }

                    }
                } else if (loveList.size() == 0) {  //点赞，并且数据库无记录的
                    Love like = new Love();
                    like.setType(num[0].trim());
                    like.setTypeid(Integer.valueOf(num[1].trim()));
                    like.setUserid(Integer.valueOf(num[2].trim()));
                    like.setCreatetime(DateUtils.dateByString());
                    this.likeMapper.insert(like);
                    if (num[0].trim().equals("reply")) {
                        this.likeMapper.like(Integer.valueOf(num[1].trim()), 1);
                    } else {
                        this.likeMapper.like1(Integer.valueOf(num[1].trim()), 1);
                    }
                }
            }
        }
    }
}
