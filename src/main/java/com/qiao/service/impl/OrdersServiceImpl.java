package com.qiao.service.impl;

import com.qiao.common.BaseContext;
import com.qiao.common.CustomException;
import com.qiao.dto.OrdersDto;
import com.qiao.entity.*;
import com.qiao.repository.OrderDetailRepository;
import com.qiao.repository.OrdersRepository;
import com.qiao.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        Long userId = BaseContext.getCurrentId();

        // 1. 获取购物车
        List<ShoppingCart> cartList = shoppingCartService.list(userId);
        if (cartList == null || cartList.isEmpty()) {
            throw new CustomException("Shopping cart is empty");
        }

        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        // 2. 准备订单主表数据 (不要手动 set ID)
        orders.setNumber(UUID.randomUUID().toString().replace("-", "")); // 使用唯一编号
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        // 计算总金额
        BigDecimal amount = BigDecimal.ZERO;
        for (ShoppingCart cart : cartList) {
            amount = amount.add(cart.getAmount().multiply(new BigDecimal(cart.getNumber())));
        }
        orders.setAmount(amount);

        // 3. 关键：先保存订单，获取数据库生成的真实 ID
        Orders savedOrder = ordersRepository.save(orders);
        Long realOrderId = savedOrder.getId();

        // 4. 构建并保存明细
        List<OrderDetail> details = new ArrayList<>();
        cartList.forEach(item -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(realOrderId); // 使用数据库返回的真实 ID
            detail.setNumber(item.getNumber());
            detail.setDishFlavor(item.getDishFlavor());
            detail.setDishId(item.getDishId());
            detail.setSetmealId(item.getSetmealId());
            detail.setName(item.getName());
            detail.setImage(item.getImage());
            detail.setAmount(item.getAmount());
            details.add(detail);
        });

        orderDetailRepository.saveAll(details);

        // 5. 清理购物车
        shoppingCartService.clean(userId);
    }

    @Override
    @Transactional
    public void again(Orders orders) {
        // Find old order details
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orders.getId());

        // Convert to shopping cart items
        details.forEach(detail -> {
            ShoppingCart cart = new ShoppingCart();
            cart.setName(detail.getName());
            cart.setImage(detail.getImage());
            cart.setUserId(BaseContext.getCurrentId());
            cart.setDishId(detail.getDishId());
            cart.setSetmealId(detail.getSetmealId());
            cart.setDishFlavor(detail.getDishFlavor());
            cart.setNumber(detail.getNumber());
            cart.setAmount(detail.getAmount());
            cart.setCreateTime(LocalDateTime.now());
            shoppingCartService.add(cart);
        });
    }

    @Override
    public Page<OrdersDto> page(int page, int pageSize, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("orderTime").descending());
        Page<Orders> ordersPage = ordersRepository.findByUserId(userId, pageable);

        return ordersPage.map(order -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(order, dto); // 复制基础属性

            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
            dto.setOrderDetails(details);

            return dto;
        });
    }
}