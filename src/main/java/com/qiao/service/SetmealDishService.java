package com.qiao.service;

import com.qiao.entity.SetmealDish;
import java.util.List;

public interface SetmealDishService {
    void saveBatch(List<SetmealDish> setmealDishes);
}