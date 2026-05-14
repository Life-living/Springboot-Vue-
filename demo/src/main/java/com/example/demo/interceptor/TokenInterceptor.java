package com.example.demo.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.Mapper.UserTokenMapper;
import com.example.demo.Pojo.UserToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserTokenMapper userTokenMapper;

    /**
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        System.out.println("🔍 当前请求: " + request.getRequestURI());
        System.out.println("🔐 Authorization Header: " + token);  // 打印 token

        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("Not Login");
            return false;
        }

        // 查询 token 是否有效（未过期）
        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token)
                .gt("expire_time", LocalDateTime.now());
        UserToken userToken = userTokenMapper.selectOne(queryWrapper);
        System.out.println(userToken+"____________________________");

        if (userToken != null) {
            // 可选：将用户 ID 存入请求，后续可用
            request.setAttribute("userId", userToken.getUserId());
            return true;
        } else {
            response.setStatus(401);
            response.getWriter().write("Login expired or invalid");
            return false;
        }
    }
}