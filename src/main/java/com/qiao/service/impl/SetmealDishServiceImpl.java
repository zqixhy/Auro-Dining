package com.qiao.service.impl;

import com.qiao.entity.SetmealDish;
import com.qiao.repository.SetmealDishRepository;
import com.qiao.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealDishServiceImpl implements SetmealDishService {

    @Autowired
    private SetmealDishRepository setmealDishRepository;

    @Override
    public void saveBatch(List<SetmealDish> setmealDishes) {
        setmealDishRepository.saveAll(setmealDishes);
    }
}
