package com.qiao.service;

import com.qiao.dto.SetmealDto;
import com.qiao.entity.Setmeal;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SetmealService {

    Setmeal getById(Long id);
    void update(Setmeal setmeal);
    List<Setmeal> list(Setmeal setmeal);

    Page<Setmeal> page(int page, int pageSize, String name);

    void saveMeal(SetmealDto setmealDto);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDish(List<Long> ids);
}
