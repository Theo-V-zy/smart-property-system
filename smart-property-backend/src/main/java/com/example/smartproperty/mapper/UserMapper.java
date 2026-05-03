package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Insert("""
            insert into sys_user(role, username, password_hash, name, phone, community_name, address, business_scope, avatar, created_at, updated_at)
            values(#{role}, #{username}, #{passwordHash}, #{name}, #{phone}, #{communityName}, #{address}, #{businessScope}, #{avatar}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("select * from sys_user where username = #{username} and role = #{role} limit 1")
    User findByUsernameAndRole(@Param("username") String username, @Param("role") String role);

    @Select("select * from sys_user where id = #{id}")
    User findById(Long id);

    @Update("""
            update sys_user
            set name = #{name},
                phone = #{phone},
                address = #{address},
                business_scope = #{businessScope},
                avatar = #{avatar},
                updated_at = now()
            where id = #{id}
            """)
    int updateProfile(User user);

    @Update("update sys_user set password_hash = #{passwordHash}, updated_at = now() where id = #{id}")
    int updatePassword(User user);
}
