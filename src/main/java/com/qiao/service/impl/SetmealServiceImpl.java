package com.qiao.service.impl;

import com.qiao.common.CustomException;
import com.qiao.dto.SetmealDto;
import com.qiao.entity.Setmeal;
import com.qiao.entity.SetmealDish;
import com.qiao.repository.SetmealDishRepository;
import com.qiao.repository.SetmealRepository;
import com.qiao.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealRepository setmealRepository;

    @Autowired
    private SetmealDishRepository setmealDishRepository;

    /**
     * Save new Setmeal
     */
    @Override
    @Transactional
    public void saveMeal(SetmealDto setmealDto) {
        Setmeal setmeal = setmealRepository.save(setmealDto);

        // Save dishes
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(item -> item.setSetmealId(setmeal.getId().toString()));

        setmealDishRepository.saveAll(setmealDishes);
    }

    /**
     * Get Setmeal + Dishes (for edit)
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = setmealRepository.findById(id).orElse(null);
        if(setmeal == null) return null;

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        List<SetmealDish> dishes = setmealDishRepository.findBySetmealId(id.toString());
        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    /**
     * Update Setmeal
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        setmealRepository.save(setmealDto);

        setmealDishRepository.deleteBySetmealId(setmealDto.getId().toString());

        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes.forEach(item -> item.setSetmealId(setmealDto.getId().toString()));

        setmealDishRepository.saveAll(dishes);
    }

    /**
     * Delete Setmeal (Check status first)
     */
    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        // heck if any setmeal is "On Sale" (status = 1)
        List<Setmeal> setmeals = setmealRepository.findAllById(ids);
        for (Setmeal s : setmeals) {
            if (s.getStatus() == 1) {
                throw new CustomException("Combo is on sale, cannot delete!");
            }
        }

        setmealRepository.deleteAllById(ids);

        // Delete related dishes (Convert Long IDs to String IDs)
        List<String> stringIds = ids.stream().map(String::valueOf).collect(Collectors.toList());
        setmealDishRepository.deleteBySetmealIdIn(stringIds);
    }

    @Override
    public Page<Setmeal> page(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("updateTime").descending());
        if(name != null){
            return setmealRepository.findByNameContaining(name, pageable);
        }
        return setmealRepository.findAll(pageable);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealRepository.findByCategoryId(setmeal.getCategoryId());
    }

    @Override
    public Setmeal getById(Long id) {
        return setmealRepository.findById(id).orElse(null);
    }

    @Override
    public void update(Setmeal setmeal) {
        setmealRepository.save(setmeal);
    }
}