package com.example.smartproperty.mapper;

import com.example.smartproperty.entity.LostFoundItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LostFoundMapper {

    @Insert("""
            insert into lost_found_item(type, title, description, pickup_location, contact_name, contact_phone, images_json, status, publisher_id, publisher_name, created_at, updated_at)
            values(#{type}, #{title}, #{description}, #{pickupLocation}, #{contactName}, #{contactPhone}, #{imagesJson}, #{status}, #{publisherId}, #{publisherName}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LostFoundItem item);

    @Select("""
            <script>
            select * from lost_found_item
            <where>
                <if test="type != null and type != ''">
                    type = #{type}
                </if>
            </where>
            order by updated_at desc, id desc
            </script>
            """)
    List<LostFoundItem> findAll(@Param("type") String type);
}
