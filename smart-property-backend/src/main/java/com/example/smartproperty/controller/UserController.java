package com.example.smartproperty.controller;

import com.example.smartproperty.common.ApiResponse;
import com.example.smartproperty.dto.PasswordUpdateRequest;
import com.example.smartproperty.dto.ProfileUpdateRequest;
import com.example.smartproperty.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> profile() {
        return ApiResponse.success(userService.profile());
    }

    @GetMapping("/owners")
    public ApiResponse<List<Map<String, Object>>> owners(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(userService.ownerList(keyword));
    }

    @GetMapping("/owners/{id}")
    public ApiResponse<Map<String, Object>> ownerDetail(@PathVariable Long id) {
        return ApiResponse.success(userService.ownerDetail(id));
    }

    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(request);
        return ApiResponse.success("资料已更新", null);
    }

    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(request);
        return ApiResponse.success("密码修改成功", null);
    }
}
