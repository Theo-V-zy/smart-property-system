package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.BusinessException;
import com.example.smartproperty.common.SecurityUtils;
import com.example.smartproperty.dto.PasswordUpdateRequest;
import com.example.smartproperty.dto.ProfileUpdateRequest;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.UserMapper;
import com.example.smartproperty.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Map<String, Object> profile() {
        User user = userMapper.findById(AuthContext.getUserId());
        return AuthServiceImpl.buildUserView(user);
    }

    @Override
    public void updateProfile(ProfileUpdateRequest request) {
        User user = userMapper.findById(AuthContext.getUserId());
        user.setName(blankToOriginal(request.getName(), user.getName()));
        user.setPhone(blankToOriginal(request.getPhone(), user.getPhone()));
        user.setAddress(blankToOriginal(request.getAddress(), user.getAddress()));
        user.setBusinessScope(blankToOriginal(request.getBusinessScope(), user.getBusinessScope()));
        user.setAvatar(blankToOriginal(request.getAvatar(), user.getAvatar()));
        userMapper.updateProfile(user);
    }

    @Override
    public void updatePassword(PasswordUpdateRequest request) {
        User user = userMapper.findById(AuthContext.getUserId());
        if (!SecurityUtils.md5(request.getOldPassword()).equals(user.getPasswordHash())) {
            throw new BusinessException("旧密码不正确");
        }
        user.setPasswordHash(SecurityUtils.md5(request.getNewPassword()));
        userMapper.updatePassword(user);
    }

    private String blankToOriginal(String next, String original) {
        return next == null || next.isBlank() ? original : next;
    }
}
