package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.dto.ProfileUpdateRequest;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void tearDown() {
        AuthContext.clear();
    }

    @Test
    void shouldIgnoreBusinessScopeWhenCurrentUserIsOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole(RoleConstants.OWNER);
        owner.setName("张三");
        owner.setBusinessScope("历史值");
        AuthContext.setUser(owner);
        when(userMapper.findById(1L)).thenReturn(owner);

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setName("张三");
        request.setBusinessScope("水电维修");

        userService.updateProfile(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateProfile(captor.capture());
        assertEquals(null, captor.getValue().getBusinessScope());
    }

    @Test
    void shouldKeepBusinessScopeEditableForStaff() {
        User staff = new User();
        staff.setId(2L);
        staff.setRole(RoleConstants.STAFF);
        staff.setName("李师傅");
        staff.setBusinessScope("门禁维修");
        AuthContext.setUser(staff);
        when(userMapper.findById(2L)).thenReturn(staff);

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setBusinessScope("水电维修");

        userService.updateProfile(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateProfile(captor.capture());
        assertEquals("水电维修", captor.getValue().getBusinessScope());
    }
}
