package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.ServiceEvaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EvaluationMapper {

    @Insert("""
            insert into service_evaluation(order_id, owner_id, service_score, quality_score, speed_score, content, images_json, created_at)
            values(#{orderId}, #{ownerId}, #{serviceScore}, #{qualityScore}, #{speedScore}, #{content}, #{imagesJson}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceEvaluation evaluation);

    @Select("select * from service_evaluation where order_id = #{orderId} limit 1")
    ServiceEvaluation findByOrderId(Long orderId);
}
