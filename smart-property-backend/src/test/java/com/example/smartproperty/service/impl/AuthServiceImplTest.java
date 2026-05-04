package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.BusinessException;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.common.SecurityUtils;
import com.example.smartproperty.dto.LoginRequest;
import com.example.smartproperty.dto.OwnerRegisterRequest;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.entity.UserSession;
import com.example.smartproperty.mapper.SessionMapper;
import com.example.smartproperty.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldReturnTokenAndUserInfoWhenLoginSucceeds() {
        User user = new User();
        user.setId(1L);
        user.setRole(RoleConstants.OWNER);
        user.setUsername("owner01");
        user.setPasswordHash(SecurityUtils.md5("123456"));
        user.setName("张三");
        user.setPhone("13812345678");
        user.setCommunityName("幸福花园");
        user.setAddress("1栋1单元101");
        when(userMapper.findByUsernameAndRole("owner01", RoleConstants.OWNER)).thenReturn(user);

        LoginRequest request = new LoginRequest();
        request.setRole(RoleConstants.OWNER);
        request.setUsername("owner01");
        request.setPassword("123456");

        Map<String, Object> result = authService.login(request);

        assertNotNull(result.get("token"));
        assertEquals("张三", ((Map<?, ?>) result.get("user")).get("name"));
        verify(sessionMapper, times(1)).deleteByUserId(1L);

        ArgumentCaptor<UserSession> sessionCaptor = ArgumentCaptor.forClass(UserSession.class);
        verify(sessionMapper).insert(sessionCaptor.capture());
        assertEquals(1L, sessionCaptor.getValue().getUserId());
        assertNotNull(sessionCaptor.getValue().getToken());
    }

    @Test
    void shouldThrowWhenPasswordIsInvalid() {
        User user = new User();
        user.setRole(RoleConstants.OWNER);
        user.setUsername("owner01");
        user.setPasswordHash(SecurityUtils.md5("123456"));
        when(userMapper.findByUsernameAndRole("owner01", RoleConstants.OWNER)).thenReturn(user);

        LoginRequest request = new LoginRequest();
        request.setRole(RoleConstants.OWNER);
        request.setUsername("owner01");
        request.setPassword("bad-password");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals("账号或密码错误", exception.getMessage());
    }

    @Test
    void shouldThrowWhenOwnerUsernameAlreadyExists() {
        User existing = new User();
        existing.setId(99L);
        when(userMapper.findByUsernameAndRole(eq("owner01"), eq(RoleConstants.OWNER))).thenReturn(existing);

        OwnerRegisterRequest request = new OwnerRegisterRequest();
        request.setUsername("owner01");
        request.setPassword("123456");
        request.setName("张三");
        request.setPhone("13812345678");
        request.setCommunityName("幸福花园");
        request.setAddress("1栋1单元101");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.registerOwner(request));

        assertEquals("该账号已存在", exception.getMessage());
    }
}
