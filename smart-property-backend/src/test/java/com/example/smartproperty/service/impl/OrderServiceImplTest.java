package com.example.smartproperty.service.impl;

import com.example.smartproperty.common.AuthContext;
import com.example.smartproperty.common.BusinessException;
import com.example.smartproperty.common.RoleConstants;
import com.example.smartproperty.dto.EvaluationCreateRequest;
import com.example.smartproperty.dto.OrderCreateRequest;
import com.example.smartproperty.dto.OrderProcessRequest;
import com.example.smartproperty.entity.ServiceOrder;
import com.example.smartproperty.entity.User;
import com.example.smartproperty.mapper.EvaluationMapper;
import com.example.smartproperty.mapper.ServiceOrderMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private ServiceOrderMapper orderMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @AfterEach
    void tearDown() {
        AuthContext.clear();
    }

    @Test
    void shouldRejectCreateWhenCurrentUserIsNotOwner() {
        User staff = new User();
        staff.setId(2L);
        staff.setRole(RoleConstants.STAFF);
        AuthContext.setUser(staff);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setCategory("REPAIR");
        request.setSubtype("水电维修");
        request.setDescription("厨房漏水");
        request.setAddress("1栋1单元101");
        request.setImages(List.of());

        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.create(request));

        assertEquals("仅业主可以提交工单", exception.getMessage());
    }

    @Test
    void shouldCreatePendingOrderForOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole(RoleConstants.OWNER);
        owner.setName("张三");
        owner.setPhone("13812345678");
        AuthContext.setUser(owner);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setCategory("REPAIR");
        request.setSubtype("水电维修");
        request.setDescription("厨房漏水");
        request.setAddress("1栋1单元101");
        request.setImages(List.of("img1"));

        orderService.create(request);

        ArgumentCaptor<ServiceOrder> captor = ArgumentCaptor.forClass(ServiceOrder.class);
        verify(orderMapper).insert(captor.capture());
        assertEquals("PENDING", captor.getValue().getStatus());
        assertEquals("张三", captor.getValue().getOwnerName());
        assertEquals("1栋1单元101", captor.getValue().getAddress());
    }

    @Test
    void shouldRejectEvaluationWhenOrderNotCompleted() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole(RoleConstants.OWNER);
        AuthContext.setUser(owner);

        ServiceOrder order = new ServiceOrder();
        order.setId(10L);
        order.setOwnerId(1L);
        order.setStatus("PROCESSING");
        when(orderMapper.findById(10L)).thenReturn(order);

        EvaluationCreateRequest request = new EvaluationCreateRequest();
        request.setServiceScore(5);
        request.setQualityScore(5);
        request.setSpeedScore(5);
        request.setContent("很好");

        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.evaluate(10L, request));

        assertEquals("仅已完成工单可评价", exception.getMessage());
    }

    @Test
    void shouldUpdateHandlerInfoWhenStaffProcessesOrder() {
        User staff = new User();
        staff.setId(2L);
        staff.setRole(RoleConstants.STAFF);
        staff.setName("李师傅");
        AuthContext.setUser(staff);

        ServiceOrder order = new ServiceOrder();
        order.setId(11L);
        when(orderMapper.findById(11L)).thenReturn(order);

        OrderProcessRequest request = new OrderProcessRequest();
        request.setStatus("PROCESSING");
        request.setHandleRecord("已联系住户");
        request.setReply("明天上门");

        orderService.process(11L, request);

        ArgumentCaptor<ServiceOrder> captor = ArgumentCaptor.forClass(ServiceOrder.class);
        verify(orderMapper).updateProcess(captor.capture());
        assertEquals("李师傅", captor.getValue().getHandlerName());
        assertEquals("PROCESSING", captor.getValue().getStatus());
    }
}
