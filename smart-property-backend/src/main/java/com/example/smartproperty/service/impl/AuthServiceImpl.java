package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.BusinessException;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.common.SecurityUtils;
import com.example.smartproperty.dto.LoginRequest;
import com.example.smartproperty.dto.OwnerRegisterRequest;
import com.example.smartproperty.dto.StaffRegisterRequest;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.entity.UserSession;
import com.example.smartproperty.mapper.SessionMapper;
import com.example.smartproperty.mapper.UserMapper;
import com.example.smartproperty.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final SessionMapper sessionMapper;

    public AuthServiceImpl(UserMapper userMapper, SessionMapper sessionMapper) {
        this.userMapper = userMapper;
        this.sessionMapper = sessionMapper;
    }

    @Override
    public void registerOwner(OwnerRegisterRequest request) {
        ensureUserNotExists(request.getUsername(), RoleConstants.OWNER);
        User user = new User();
        user.setRole(RoleConstants.OWNER);
        user.setUsername(request.getUsername());
        user.setPasswordHash(SecurityUtils.md5(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setCommunityName(request.getCommunityName());
        user.setAddress(request.getAddress());
        user.setAvatar(defaultAvatar());
        userMapper.insert(user);
    }

    @Override
    public void registerStaff(StaffRegisterRequest request) {
        ensureUserNotExists(request.getUsername(), RoleConstants.STAFF);
        User user = new User();
        user.setRole(RoleConstants.STAFF);
        user.setUsername(request.getUsername());
        user.setPasswordHash(SecurityUtils.md5(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setBusinessScope(request.getBusinessScope());
        user.setAvatar(defaultAvatar());
        userMapper.insert(user);
    }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        User user = userMapper.findByUsernameAndRole(request.getUsername(), request.getRole());
        if (user == null || !SecurityUtils.md5(request.getPassword()).equals(user.getPasswordHash())) {
            throw new BusinessException("账号或密码错误");
        }
        sessionMapper.deleteByUserId(user.getId());
        String token = UUID.randomUUID().toString().replace("-", "");
        UserSession session = new UserSession();
        session.setUserId(user.getId());
        session.setToken(token);
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        sessionMapper.insert(session);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", buildUserView(user));
        return result;
    }

    @Override
    public Map<String, Object> currentUser() {
        User user = AuthContext.getUser();
        if (user == null) {
            throw new BusinessException("未登录");
        }
        return buildUserView(user);
    }

    @Override
    public void logout(String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            sessionMapper.deleteByToken(authorization.substring(7));
        }
    }

    private void ensureUserNotExists(String username, String role) {
        if (userMapper.findByUsernameAndRole(username, role) != null) {
            throw new BusinessException("该账号已存在");
        }
    }

    static Map<String, Object> buildUserView(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("role", user.getRole());
        data.put("username", user.getUsername());
        data.put("name", user.getName());
        data.put("phone", user.getPhone());
        data.put("communityName", user.getCommunityName());
        data.put("address", user.getAddress());
        data.put("businessScope", user.getBusinessScope());
        data.put("avatar", user.getAvatar());
        return data;
    }

    private String defaultAvatar() {
        return "";
    }
}
