package com.example.smartproperty.service;

import com.example.smartproperty.dto.LoginRequest;
import com.example.smartproperty.dto.OwnerRegisterRequest;

import java.util.Map;

public interface AuthService {
    void registerOwner(OwnerRegisterRequest request);

    Map<String, Object> login(LoginRequest request);

    Map<String, Object> currentUser();

    void logout(String authorization);
}
