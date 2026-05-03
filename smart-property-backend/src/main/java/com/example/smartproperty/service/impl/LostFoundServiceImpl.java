package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.BusinessException;
import com.example.smartproperty.common.JsonUtils;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.dto.LostFoundCreateRequest;
import com.example.smartproperty.entity.LostFoundItem;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.LostFoundMapper;
import com.example.smartproperty.service.LostFoundService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LostFoundServiceImpl implements LostFoundService {

    private final LostFoundMapper lostFoundMapper;

    public LostFoundServiceImpl(LostFoundMapper lostFoundMapper) {
        this.lostFoundMapper = lostFoundMapper;
    }

    @Override
    public List<Map<String, Object>> list(String type) {
        return lostFoundMapper.findAll(type).stream().map(this::toMap).toList();
    }

    @Override
    public void create(LostFoundCreateRequest request) {
        User user = AuthContext.getUser();
        if (!RoleConstants.STAFF.equals(user.getRole())) {
            throw new BusinessException("仅物业人员可以发布失物招领");
        }
        LostFoundItem item = new LostFoundItem();
        item.setType(request.getType());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setPickupLocation(request.getPickupLocation());
        item.setContactName(request.getContactName());
        item.setContactPhone(request.getContactPhone());
        item.setImagesJson(JsonUtils.toJson(request.getImages()));
        item.setStatus("PUBLISHED");
        item.setPublisherId(user.getId());
        item.setPublisherName(user.getName());
        lostFoundMapper.insert(item);
    }

    private Map<String, Object> toMap(LostFoundItem item) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", item.getId());
        data.put("type", item.getType());
        data.put("title", item.getTitle());
        data.put("description", item.getDescription());
        data.put("pickupLocation", item.getPickupLocation());
        data.put("contactName", item.getContactName());
        data.put("contactPhone", item.getContactPhone());
        data.put("images", JsonUtils.toList(item.getImagesJson()));
        data.put("status", item.getStatus());
        data.put("publisherName", item.getPublisherName());
        data.put("createdAt", item.getCreatedAt());
        return data;
    }
}
