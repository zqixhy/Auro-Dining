package com.qiao.repository;

import com.qiao.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // Find all details for a specific order
    List<OrderDetail> findByOrderId(Long orderId);
}