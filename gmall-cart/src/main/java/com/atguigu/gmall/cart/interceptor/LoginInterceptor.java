package com.atguigu.gmall.cart.interceptor;

import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.cart.property.JwtProperties;
import com.atguigu.gmall.cart.vo.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtProperties jwtProperties;
    private static final ThreadLocal<UserInfo> THREAD_LOCAL=new ThreadLocal<>();

    public static UserInfo getUserInfo(){
        UserInfo userInfo = THREAD_LOCAL.get();
        return  userInfo;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfo userInfo = new UserInfo();
        // 获取cookie信息
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        String userKey = CookieUtils.getCookieValue(request, jwtProperties.getUserKeyName());
        userInfo.setUserKey(userKey);
        // 如果都为空，设置userKey。
        if (StringUtils.isBlank(token) && StringUtils.isBlank(userKey)){
            userKey = UUID.randomUUID().toString();
            CookieUtils.setCookie(request, response, jwtProperties.getUserKeyName(), userKey, jwtProperties.getExpire());
            // 不管有没有登录都要设置userKey

            userInfo.setUserKey(userKey);
        }else {
            // token不为空,解析token

            try {
                Map<String, Object> map = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
                if (!CollectionUtils.isEmpty(map)) {
                    userInfo.setId(new Long(map.get("id").toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // 保存到threadlocal
        THREAD_LOCAL.set(userInfo);

        // 如果token不为空，
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        THREAD_LOCAL.remove();
    }
}
