package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.UserSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface SessionMapper {

    int insert(UserSession session);

    Long findUserIdByToken(@Param("token") String token, @Param("now") LocalDateTime now);

    int deleteByToken(@Param("token") String token);

    int deleteByUserId(@Param("userId") Long userId);
}
