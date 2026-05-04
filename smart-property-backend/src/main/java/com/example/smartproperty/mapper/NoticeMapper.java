package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {

    int insert(Notice notice);

    List<Notice> findAll();
}
