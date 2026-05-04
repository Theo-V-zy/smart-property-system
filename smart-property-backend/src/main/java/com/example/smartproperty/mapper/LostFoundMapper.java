package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.LostFoundItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LostFoundMapper {

    int insert(LostFoundItem item);

    List<LostFoundItem> findAll(@Param("type") String type);
}
