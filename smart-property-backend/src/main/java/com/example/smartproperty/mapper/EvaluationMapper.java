package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.ServiceEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EvaluationMapper {

    int insert(ServiceEvaluation evaluation);

    ServiceEvaluation findByOrderId(@Param("orderId") Long orderId);
}
