package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    int insert(User user);

    User findByUsernameAndRole(@Param("username") String username, @Param("role") String role);

    User findById(@Param("id") Long id);

    List<User> findOwners(@Param("keyword") String keyword);

    User findByIdAndRole(@Param("id") Long id, @Param("role") String role);

    int updateProfile(User user);

    int updatePassword(User user);
}
