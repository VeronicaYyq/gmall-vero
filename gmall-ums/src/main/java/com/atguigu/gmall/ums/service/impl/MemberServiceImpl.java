package com.atguigu.gmall.ums.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;



@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1:
                wrapper.eq("username", data);
                break;
            case 2:
                wrapper.eq("mobile", data);
                break;
            case 3:
                wrapper.eq("email", data);
                break;
            default:
                return null;
        }

        Integer integer = memberDao.selectCount(wrapper);
        if(integer>0)
            return true;
        else
            return false;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
        //1.校验验证码，和redis中存储的验证码进行比对
        if(code.equals(redisTemplate.opsForValue().get("code:"+memberEntity.getMobile()))){
            //2.生成盐并存储到数据库中
            String salt= UUID.randomUUID().toString().substring(0,5);
            memberEntity.setSalt(salt);

            //3.加盐加密(使用MD5的方式),存储到数据库
            String password = DigestUtils.md5Hex(memberEntity.getPassword()+salt);
            memberEntity.setPassword(password);

            //4.设置其他的属性
            memberEntity.setCreateTime(new Date());

            int insert = memberDao.insert(memberEntity);
            //5.删除redis中的验证码
            if(insert>0){
                redisTemplate.delete("code:"+memberEntity.getMobile());

            }

        }



    }

    @Override
    public MemberEntity query(String username, String password) throws IllegalAccessException {



        // 先根据用户名查询用户信息
        MemberEntity user = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));

        // 判断用户是否为空
        if (user == null) {
            throw new IllegalAccessException("用户名不存在！");
        }

        // 获取用户信息中的盐，对登陆时的密码进行相同方式的加密
        password = DigestUtils.md5Hex(password + user.getSalt());

        // 比较登录密码和数据库密码是否一致
        if (!StringUtils.equals(password, user.getPassword())){
            throw new IllegalAccessException("密码不正确！");
        }

        return user;
    }

}