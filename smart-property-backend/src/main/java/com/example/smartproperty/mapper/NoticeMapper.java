package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.Notice;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoticeMapper {

    @Insert("""
            insert into notice(title, content, publisher_id, publisher_name, pinned, created_at)
            values(#{title}, #{content}, #{publisherId}, #{publisherName}, #{pinned}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    @Select("select * from notice order by pinned desc, created_at desc, id desc")
    List<Notice> findAll();
}
