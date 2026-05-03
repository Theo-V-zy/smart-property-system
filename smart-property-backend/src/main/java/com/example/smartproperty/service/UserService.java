package com.example.smartproperty.service;

import com.example.smartproperty.dto.PasswordUpdateRequest;
import com.example.smartproperty.dto.ProfileUpdateRequest;

import java.util.Map;

public interface UserService {
    Map<String, Object> profile();

    void updateProfile(ProfileUpdateRequest request);

    void updatePassword(PasswordUpdateRequest request);
}
