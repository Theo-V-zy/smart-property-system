package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.ServiceOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ServiceOrderMapper {

    @Insert("""
            insert into service_order(order_no, owner_id, owner_name, phone, category, subtype, description, images_json, address, status, reply, handler_id, handler_name, handle_record, created_at, updated_at)
            values(#{orderNo}, #{ownerId}, #{ownerName}, #{phone}, #{category}, #{subtype}, #{description}, #{imagesJson}, #{address}, #{status}, #{reply}, #{handlerId}, #{handlerName}, #{handleRecord}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceOrder order);

    @Select("""
            <script>
            select * from service_order
            <where>
                <if test="ownerId != null">
                    owner_id = #{ownerId}
                </if>
                <if test="status != null and status != ''">
                    and status = #{status}
                </if>
                <if test="category != null and category != ''">
                    and category = #{category}
                </if>
            </where>
            order by updated_at desc, id desc
            </script>
            """)
    List<ServiceOrder> findList(@Param("ownerId") Long ownerId, @Param("status") String status, @Param("category") String category);

    @Select("select * from service_order where id = #{id}")
    ServiceOrder findById(Long id);

    @Update("""
            update service_order
            set status = #{status},
                reply = #{reply},
                handle_record = #{handleRecord},
                handler_id = #{handlerId},
                handler_name = #{handlerName},
                updated_at = now()
            where id = #{id}
            """)
    int updateProcess(ServiceOrder order);

    @Select("select count(*) from service_order where status = #{status}")
    Integer countByStatus(@Param("status") String status);

    @Select("select count(*) from service_order where owner_id = #{ownerId} and status = #{status}")
    Integer countByOwnerAndStatus(@Param("ownerId") Long ownerId, @Param("status") String status);
}
