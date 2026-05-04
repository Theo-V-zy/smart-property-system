package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.BusinessException;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.common.SecurityUtils;
import com.example.smartproperty.dto.PasswordUpdateRequest;
import com.example.smartproperty.dto.ProfileUpdateRequest;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.UserMapper;
import com.example.smartproperty.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<Map<String, Object>> ownerList(String keyword) {
        ensureStaff();
        return userMapper.findOwners(keyword).stream()
                .map(AuthServiceImpl::buildUserView)
                .toList();
    }

    @Override
    public Map<String, Object> ownerDetail(Long id) {
        ensureStaff();
        User user = userMapper.findByIdAndRole(id, RoleConstants.OWNER);
        if (user == null) {
            throw new BusinessException("住户不存在");
        }
        return AuthServiceImpl.buildUserView(user);
    }

    @Override
    public void updateProfile(ProfileUpdateRequest request) {
        User user = userMapper.findById(AuthContext.getUserId());
        user.setName(blankToOriginal(request.getName(), user.getName()));
        user.setPhone(blankToOriginal(request.getPhone(), user.getPhone()));
        user.setAddress(blankToOriginal(request.getAddress(), user.getAddress()));
        if (RoleConstants.STAFF.equals(user.getRole())) {
            user.setBusinessScope(blankToOriginal(request.getBusinessScope(), user.getBusinessScope()));
        } else {
            user.setBusinessScope(null);
        }
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

    private void ensureStaff() {
        User currentUser = userMapper.findById(AuthContext.getUserId());
        if (currentUser == null || !RoleConstants.STAFF.equals(currentUser.getRole())) {
            throw new BusinessException("仅物业人员可查看住户信息");
        }
    }
}
