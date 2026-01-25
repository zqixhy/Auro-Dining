package com.qiao.controller;

import com.qiao.common.BaseContext;
import com.qiao.common.R;
import com.qiao.entity.ShoppingCart;
import com.qiao.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * Get cart list for current user
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list(BaseContext.getCurrentId());
        return R.success(list);
    }

    /**
     * Add item to cart
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart cartItem = shoppingCartService.add(shoppingCart);
        return R.success(cartItem);
    }

    /**
     * Remove one item or decrease number
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.sub(shoppingCart, BaseContext.getCurrentId());
        return R.success("update success");
    }

    /**
     * Clean all items in cart
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        shoppingCartService.clean(BaseContext.getCurrentId());
        return R.success("delete success");
    }
}