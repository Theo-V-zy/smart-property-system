package com.example.smartproperty.service;

import com.example.smartproperty.dto.PasswordUpdateRequest;
import com.example.smartproperty.dto.ProfileUpdateRequest;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> profile();

    List<Map<String, Object>> ownerList(String keyword);

    Map<String, Object> ownerDetail(Long id);

    void updateProfile(ProfileUpdateRequest request);

    void updatePassword(PasswordUpdateRequest request);
}
