package com.example.smartproperty.config;

import com.example.smartproperty.common.ApiResponse;
import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.SessionMapper;
import com.example.smartproperty.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionMapper sessionMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(SessionMapper sessionMapper, UserMapper userMapper, ObjectMapper objectMapper) {
        this.sessionMapper = sessionMapper;
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return false;
        }
        String token = authorization.substring(7);
        Long userId = sessionMapper.findUserIdByToken(token, LocalDateTime.now());
        if (userId == null) {
            writeUnauthorized(response);
            return false;
        }
        User user = userMapper.findById(userId);
        if (user == null) {
            writeUnauthorized(response);
            return false;
        }
        AuthContext.setUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(401);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(401, "登录已失效，请重新登录")));
    }
}
