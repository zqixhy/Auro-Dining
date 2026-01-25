package com.qiao.service.impl;

import com.qiao.entity.ShoppingCart;
import com.qiao.repository.ShoppingCartRepository;
import com.qiao.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<ShoppingCart> list(Long userId) {
        return shoppingCartRepository.findByUserIdOrderByCreateTimeAsc(userId);
    }

    @Override
    @Transactional
    public ShoppingCart add(ShoppingCart shoppingCart) {
        Long userId = shoppingCart.getUserId();
        ShoppingCart cartItem;

        if (shoppingCart.getDishId() != null) {
            // Check for existing dish with specific flavor
            cartItem = shoppingCartRepository.findByUserIdAndDishIdAndDishFlavor(
                    userId, shoppingCart.getDishId(), shoppingCart.getDishFlavor());
        } else {
            // Check for existing setmeal
            cartItem = shoppingCartRepository.findByUserIdAndSetmealId(userId, shoppingCart.getSetmealId());
        }

        if (cartItem != null) {
            cartItem.setNumber(cartItem.getNumber() + 1);
            return shoppingCartRepository.save(cartItem);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCartRepository.save(shoppingCart);
        }
    }

    @Override
    @Transactional
    public void clean(Long userId) {
        shoppingCartRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void sub(ShoppingCart shoppingCart, Long userId) {
        ShoppingCart cartItem;
        if (shoppingCart.getDishId() != null) {
            cartItem = shoppingCartRepository.findByUserIdAndDishIdAndDishFlavor(
                    userId, shoppingCart.getDishId(), shoppingCart.getDishFlavor());
        } else {
            cartItem = shoppingCartRepository.findByUserIdAndSetmealId(userId, shoppingCart.getSetmealId());
        }

        if (cartItem != null) {
            if (cartItem.getNumber() > 1) {
                cartItem.setNumber(cartItem.getNumber() - 1);
                shoppingCartRepository.save(cartItem);
            } else {
                shoppingCartRepository.delete(cartItem);
            }
        }
    }
}