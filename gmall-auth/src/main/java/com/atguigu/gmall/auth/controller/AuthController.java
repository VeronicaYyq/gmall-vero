package com.atguigu.gmall.auth.controller;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.RsaUtils;
import com.atguigu.gmall.auth.property.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("auth")
@EnableConfigurationProperties({JwtProperties.class})
public class AuthController {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private AuthService authService;
    @PostMapping("accredit")
    public Resp<Object> authentication(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response, HttpServletRequest request) throws Exception {
        //登录校验
        String token = authService.query(username, password);
        if(StringUtils.isEmpty(token)){
            return Resp.fail("登录失败，用户名或者密码错误");
        }
        // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        /*Cookie cookie=new Cookie(jwtProperties.getCookieName(),token);
        cookie.setMaxAge(jwtProperties.getExpire()*60);

        response.addCookie(cookie);*/
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);
        return Resp.ok(null);
    }


}
