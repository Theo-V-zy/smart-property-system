package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.BusinessException;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.dto.NoticeCreateRequest;
import com.example.smartproperty.entity.Notice;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.NoticeMapper;
import com.example.smartproperty.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public List<Map<String, Object>> list() {
        return noticeMapper.findAll().stream().map(NoticeServiceImpl::toMap).toList();
    }

    @Override
    public void create(NoticeCreateRequest request) {
        User user = AuthContext.getUser();
        if (!RoleConstants.STAFF.equals(user.getRole())) {
            throw new BusinessException("仅物业人员可以发布通知");
        }
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setPublisherId(user.getId());
        notice.setPublisherName(user.getName());
        notice.setPinned(1);
        noticeMapper.insert(notice);
    }

    static Map<String, Object> toMap(Notice notice) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", notice.getId());
        data.put("title", notice.getTitle());
        data.put("content", notice.getContent());
        data.put("publisherName", notice.getPublisherName());
        data.put("createdAt", notice.getCreatedAt());
        data.put("pinned", notice.getPinned());
        return data;
    }
}
