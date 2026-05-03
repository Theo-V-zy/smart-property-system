package com.example.smartproperty.service;

import com.example.smartproperty.dto.LoginRequest;
import com.example.smartproperty.dto.OwnerRegisterRequest;
import com.example.smartproperty.dto.StaffRegisterRequest;

import java.util.Map;

public interface AuthService {
    void registerOwner(OwnerRegisterRequest request);

    void registerStaff(StaffRegisterRequest request);

    Map<String, Object> login(LoginRequest request);

    Map<String, Object> currentUser();

    void logout(String authorization);
}
