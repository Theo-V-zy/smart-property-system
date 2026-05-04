package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int insert(User user);

    User findByUsernameAndRole(@Param("username") String username, @Param("role") String role);

    User findById(@Param("id") Long id);

    int updateProfile(User user);

    int updatePassword(User user);
}
