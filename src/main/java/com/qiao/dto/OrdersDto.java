package com.qiao.dto;

import com.qiao.entity.OrderDetail;
import com.qiao.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;

    private String userName;

    private String address;
}
