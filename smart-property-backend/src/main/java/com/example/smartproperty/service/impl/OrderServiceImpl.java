package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.BusinessException;
import com.example.smartproperty.common.JsonUtils;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.dto.EvaluationCreateRequest;
import com.example.smartproperty.dto.OrderCreateRequest;
import com.example.smartproperty.dto.OrderProcessRequest;
import com.example.smartproperty.entity.ServiceEvaluation;
import com.example.smartproperty.entity.ServiceOrder;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.EvaluationMapper;
import com.example.smartproperty.mapper.ServiceOrderMapper;
import com.example.smartproperty.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final ServiceOrderMapper orderMapper;
    private final EvaluationMapper evaluationMapper;

    public OrderServiceImpl(ServiceOrderMapper orderMapper, EvaluationMapper evaluationMapper) {
        this.orderMapper = orderMapper;
        this.evaluationMapper = evaluationMapper;
    }

    @Override
    public void create(OrderCreateRequest request) {
        User user = AuthContext.getUser();
        if (!RoleConstants.OWNER.equals(user.getRole())) {
            throw new BusinessException("仅业主可以提交工单");
        }
        ServiceOrder order = new ServiceOrder();
        order.setOrderNo("WX" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now()));
        order.setOwnerId(user.getId());
        order.setOwnerName(user.getName());
        order.setPhone(user.getPhone());
        order.setCategory(request.getCategory());
        order.setSubtype(request.getSubtype());
        order.setDescription(request.getDescription());
        order.setImagesJson(JsonUtils.toJson(request.getImages()));
        order.setAddress(request.getAddress());
        order.setStatus("PENDING");
        order.setReply("");
        order.setHandleRecord("");
        orderMapper.insert(order);
    }

    @Override
    public List<Map<String, Object>> list(String status, String category) {
        User user = AuthContext.getUser();
        Long ownerId = RoleConstants.OWNER.equals(user.getRole()) ? user.getId() : null;
        return orderMapper.findList(ownerId, status, category).stream().map(this::toMap).toList();
    }

    @Override
    public Map<String, Object> detail(Long id) {
        ServiceOrder order = requiredOrder(id);
        User user = AuthContext.getUser();
        if (RoleConstants.OWNER.equals(user.getRole()) && !user.getId().equals(order.getOwnerId())) {
            throw new BusinessException("无权查看该工单");
        }
        Map<String, Object> data = toMap(order);
        ServiceEvaluation evaluation = evaluationMapper.findByOrderId(id);
        if (evaluation != null) {
            Map<String, Object> evaluationMap = new HashMap<>();
            evaluationMap.put("serviceScore", evaluation.getServiceScore());
            evaluationMap.put("qualityScore", evaluation.getQualityScore());
            evaluationMap.put("speedScore", evaluation.getSpeedScore());
            evaluationMap.put("content", evaluation.getContent());
            evaluationMap.put("images", JsonUtils.toList(evaluation.getImagesJson()));
            evaluationMap.put("createdAt", evaluation.getCreatedAt());
            data.put("evaluation", evaluationMap);
        }
        return data;
    }

    @Override
    public void process(Long id, OrderProcessRequest request) {
        User user = AuthContext.getUser();
        if (!RoleConstants.STAFF.equals(user.getRole())) {
            throw new BusinessException("仅物业人员可以处理工单");
        }
        ServiceOrder order = requiredOrder(id);
        order.setStatus(request.getStatus());
        order.setHandleRecord(request.getHandleRecord());
        order.setReply(request.getReply());
        order.setHandlerId(user.getId());
        order.setHandlerName(user.getName());
        orderMapper.updateProcess(order);
    }

    @Override
    public void evaluate(Long id, EvaluationCreateRequest request) {
        User user = AuthContext.getUser();
        ServiceOrder order = requiredOrder(id);
        if (!RoleConstants.OWNER.equals(user.getRole()) || !user.getId().equals(order.getOwnerId())) {
            throw new BusinessException("仅工单所属业主可以评价");
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessException("仅已完成工单可评价");
        }
        if (evaluationMapper.findByOrderId(id) != null) {
            throw new BusinessException("该工单已评价");
        }
        ServiceEvaluation evaluation = new ServiceEvaluation();
        evaluation.setOrderId(id);
        evaluation.setOwnerId(user.getId());
        evaluation.setServiceScore(request.getServiceScore());
        evaluation.setQualityScore(request.getQualityScore());
        evaluation.setSpeedScore(request.getSpeedScore());
        evaluation.setContent(request.getContent());
        evaluation.setImagesJson(JsonUtils.toJson(request.getImages()));
        evaluationMapper.insert(evaluation);
    }

    private ServiceOrder requiredOrder(Long id) {
        ServiceOrder order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        return order;
    }

    private Map<String, Object> toMap(ServiceOrder order) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", order.getId());
        data.put("orderNo", order.getOrderNo());
        data.put("ownerId", order.getOwnerId());
        data.put("ownerName", order.getOwnerName());
        data.put("phone", order.getPhone());
        data.put("category", order.getCategory());
        data.put("subtype", order.getSubtype());
        data.put("description", order.getDescription());
        data.put("images", JsonUtils.toList(order.getImagesJson()));
        data.put("address", order.getAddress());
        data.put("status", order.getStatus());
        data.put("reply", order.getReply());
        data.put("handlerName", order.getHandlerName());
        data.put("handleRecord", order.getHandleRecord());
        data.put("createdAt", order.getCreatedAt());
        data.put("updatedAt", order.getUpdatedAt());
        data.put("canEvaluate", "COMPLETED".equals(order.getStatus()) && evaluationMapper.findByOrderId(order.getId()) == null);
        return data;
    }
}
