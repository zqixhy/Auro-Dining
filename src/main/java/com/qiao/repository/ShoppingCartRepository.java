package com.qiao.repository;

import com.qiao.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    List<ShoppingCart> findByUserIdOrderByCreateTimeAsc(Long userId);

    ShoppingCart findByUserIdAndDishIdAndDishFlavor(Long userId, Long dishId, String dishFlavor);

    ShoppingCart findByUserIdAndComboId(Long userId, Long comboId);

    void deleteByUserId(Long userId);
}