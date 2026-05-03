package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.LostFoundMapper;
import com.example.smartproperty.mapper.NoticeMapper;
import com.example.smartproperty.mapper.ServiceOrderMapper;
import com.example.smartproperty.service.HomeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeServiceImpl implements HomeService {

    private final NoticeMapper noticeMapper;
    private final ServiceOrderMapper orderMapper;
    private final LostFoundMapper lostFoundMapper;

    public HomeServiceImpl(NoticeMapper noticeMapper, ServiceOrderMapper orderMapper, LostFoundMapper lostFoundMapper) {
        this.noticeMapper = noticeMapper;
        this.orderMapper = orderMapper;
        this.lostFoundMapper = lostFoundMapper;
    }

    @Override
    public Map<String, Object> summary() {
        User user = AuthContext.getUser();
        Map<String, Object> data = new HashMap<>();
        if (RoleConstants.OWNER.equals(user.getRole())) {
            data.put("pendingCount", orderMapper.countByOwnerAndStatus(user.getId(), "PENDING"));
            data.put("processingCount", orderMapper.countByOwnerAndStatus(user.getId(), "PROCESSING"));
            data.put("completedCount", orderMapper.countByOwnerAndStatus(user.getId(), "COMPLETED"));
        } else {
            data.put("pendingCount", orderMapper.countByStatus("PENDING"));
            data.put("processingCount", orderMapper.countByStatus("PROCESSING"));
            data.put("completedCount", orderMapper.countByStatus("COMPLETED"));
        }
        data.put("noticeList", noticeMapper.findAll().stream().limit(4).map(NoticeServiceImpl::toMap).toList());
        data.put("lostFoundCount", lostFoundMapper.findAll(null).size());
        data.put("quickStats", List.of(
                Map.of("label", "待处理", "value", data.get("pendingCount")),
                Map.of("label", "处理中", "value", data.get("processingCount")),
                Map.of("label", "已完成", "value", data.get("completedCount"))
        ));
        return data;
    }
}
