package com.qiao.service;

import com.qiao.entity.DishFlavor;
import java.util.List;

public interface DishFlavorService {
    void saveBatch(List<DishFlavor> flavors);

    List<DishFlavor> findByDishId(Long dishId);

    void deleteByDishId(Long dishId);
}