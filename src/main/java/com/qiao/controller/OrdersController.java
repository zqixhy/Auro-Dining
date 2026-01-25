package com.qiao.controller;

import com.qiao.common.BaseContext;
import com.qiao.common.R;
import com.qiao.dto.OrdersDto;
import com.qiao.entity.OrderDetail;
import com.qiao.entity.Orders;
import com.qiao.repository.OrderDetailRepository;
import com.qiao.repository.OrdersRepository;
import com.qiao.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService orderService;

    @Autowired
    private OrdersRepository ordersRepository;

    /**
     * User order history
     */
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("/userPage")
    public R<Map<String, Object>> getUserPage(int page, int pageSize){
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by("orderTime").descending());

        Page<Orders> ordersPage = ordersRepository.findByUserId(BaseContext.getCurrentId(), pageRequest);

        List<OrdersDto> dtoList = ordersPage.getContent().stream().map(order -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(order, dto);

            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
            dto.setOrderDetails(details);

            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("records", dtoList);
        pageData.put("total", ordersPage.getTotalElements());

        return R.success(pageData);
    }

    /**
     * Admin order management with dynamic filters
     */
    @GetMapping("/page")
    public R<Map<String, Object>> getPage(int page, int pageSize, String number, String beginTime, String endTime) {

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by("orderTime").descending());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Specification<Orders> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (number != null && !number.isEmpty()) {
                predicates.add(cb.equal(root.get("number"), number));
            }
            if (beginTime != null && !beginTime.isEmpty()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("checkoutTime"), LocalDateTime.parse(beginTime, formatter)));
            }
            if (endTime != null && !endTime.isEmpty()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("checkoutTime"), LocalDateTime.parse(endTime, formatter)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Orders> ordersPage = ordersRepository.findAll(spec, pageRequest);

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("records", ordersPage.getContent());
        pageData.put("total", ordersPage.getTotalElements());

        return R.success(pageData);
    }

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("submit success");
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        orderService.again(orders);
        return R.success("success");
    }

    @PutMapping
    public R<String> editStatus(@RequestBody Orders orders){
        Orders existingOrder = ordersRepository.findById(orders.getId()).orElse(null);
        if(existingOrder != null) {
            existingOrder.setStatus(orders.getStatus());
            ordersRepository.save(existingOrder);
        }
        return R.success("edit status success");
    }
}