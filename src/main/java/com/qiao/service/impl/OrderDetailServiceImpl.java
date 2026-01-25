package com.qiao.service.impl;

import com.qiao.entity.OrderDetail;
import com.qiao.repository.OrderDetailRepository;
import com.qiao.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public void saveBatch(List<OrderDetail> orderDetails) {
        orderDetailRepository.saveAll(orderDetails);
    }

    @Override
    public List<OrderDetail> getByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}