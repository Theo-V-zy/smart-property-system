package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceOrderMapper {

    int insert(ServiceOrder order);

    List<ServiceOrder> findList(@Param("ownerId") Long ownerId, @Param("status") String status, @Param("category") String category);

    ServiceOrder findById(@Param("id") Long id);

    int updateProcess(ServiceOrder order);

    Integer countByStatus(@Param("status") String status);

    Integer countByOwnerAndStatus(@Param("ownerId") Long ownerId, @Param("status") String status);
}
