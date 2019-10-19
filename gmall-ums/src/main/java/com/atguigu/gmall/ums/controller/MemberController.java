package com.atguigu.gmall.ums.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.utils.SmsTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.experimental.PackagePrivate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;




/**
 * 会员
 *
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:33:45
 */
@Api(tags = "会员 管理")
@RestController
@RequestMapping("ums/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("query")
    public Resp<MemberEntity> query(String username,String password) throws IllegalAccessException {
        MemberEntity memberEntity=memberService.query(username,password);
        return Resp.ok(memberEntity);
    }

    @PostMapping("register")
    public Resp<Object> register(MemberEntity memberEntity,String code){
        memberService.register(memberEntity,code);
        return Resp.ok(null);
    }

    @ApiOperation("发送验证码")
    @PostMapping("code")
    /*如果该用户手机号码24小时内已经申请了3次验证码 ，
    列入黑名单[发送验证码之前 获取redis中存储的该手机号码获取验证码的次数，如果存在判断次数，如果不存在，则记录一次]*/
    public Resp<Object> sendMsg(@RequestParam("mobile") String mobile){
        //判断这个手机号码申请的次数

        String getCodeKey="code"+mobile+"count";
        String countStr=redisTemplate.opsForValue().get(getCodeKey);
        Integer count=0;
        if(countStr!=null){
            count=Integer.parseInt(countStr);
            if(count>=3){
                return Resp.ok("请不要频繁的获取验证码");
            }
        }

        //封装map传递参数
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile);
        //生成验证码
        String code= UUID.randomUUID().toString().substring(0,6).replace("-","");
        querys.put("param", "code:"+code);
        querys.put("tpl_id", "TP1711063");

        //调用短信平台给用户发送验证码
        smsTemplate.sendMs(querys);
        count++;

        //将短信验证码在服务器中保存15分钟，通过redis缓存
        String codeKey="code:"+mobile;
        redisTemplate.opsForValue().set(codeKey,code,15, TimeUnit.MINUTES);


        return Resp.ok(null);

    }
    @ApiOperation("")
    @GetMapping("/{data}/{type}")
    public Resp<Boolean> checkData(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean b= memberService.checkData(data,type);
        return Resp.ok(b);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:member:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = memberService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:member:info')")
    public Resp<MemberEntity> info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return Resp.ok(member);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:member:save')")
    public Resp<Object> save(@RequestBody MemberEntity member){
		memberService.save(member);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:member:update')")
    public Resp<Object> update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:member:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
