package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.UserSession;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface SessionMapper {

    @Insert("""
            insert into user_session(user_id, token, expires_at, created_at)
            values(#{userId}, #{token}, #{expiresAt}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserSession session);

    @Select("select user_id from user_session where token = #{token} and expires_at > #{now} limit 1")
    Long findUserIdByToken(@Param("token") String token, @Param("now") LocalDateTime now);

    @Delete("delete from user_session where token = #{token}")
    int deleteByToken(String token);

    @Delete("delete from user_session where user_id = #{userId}")
    int deleteByUserId(Long userId);
}
