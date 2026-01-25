package com.qiao.controller;

import com.qiao.common.R;
import com.qiao.dto.DishDto;
import com.qiao.entity.Category;
import com.qiao.entity.Dish;
import com.qiao.entity.DishFlavor;
import com.qiao.service.CategoryService;
import com.qiao.service.DishFlavorService;
import com.qiao.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Pagination for Dishes
     */
    @GetMapping("/page")
    public R<Map<String, Object>> getPage(int page, int pageSize, String name) {

        Page<Dish> pageInfo = dishService.page(page, pageSize, name);

        List<DishDto> dishDtoList = pageInfo.getContent().stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            Long categoryId = dish.getCategoryId();
            if (categoryId != null) {
                Category category = categoryService.getById(categoryId);
                if (category != null) {
                    dishDto.setCategoryName(category.getName());
                }
            }
            return dishDto;
        }).collect(Collectors.toList());

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("records", dishDtoList);
        pageData.put("total", pageInfo.getTotalElements());

        return R.success(pageData);
    }

    /**
     * Save Dish
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("Add dish: {}", dishDto.toString());

        dishDto.setCreateTime(LocalDateTime.now());
        dishDto.setUpdateTime(LocalDateTime.now());
        // Note: You should set CreateUser/UpdateUser here from Session like in previous modules

        dishService.saveWithFlavor(dishDto);

        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("add success");
    }

    /**
     * Update Dish
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishDto.setUpdateTime(LocalDateTime.now());

        dishService.updateWithFlavor(dishDto);

        redisTemplate.delete("dish_" + dishDto.getCategoryId() + "_1");

        return R.success("update success");
    }

    /**
     * Get Dish by ID (for Edit)
     */
    @GetMapping("/{id}")
    public R<DishDto> getByIdWithDish(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if(dishDto != null){
            return R.success(dishDto);
        }
        return R.error("failed to get dish info");
    }

    /**
     * Change Status (Bulk)
     */
    @PostMapping("/status/{status}")
    public R<String> changeDishStatus(@RequestParam List<Long> ids, @PathVariable Integer status){
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            if (dish != null) {
                dish.setStatus(status);
                dish.setUpdateTime(LocalDateTime.now());
                dishService.update(dish);

                // Clear cache for this category
                redisTemplate.delete("dish_" + dish.getCategoryId() + "_1");
            }
        }
        return R.success("change status success");
    }

    /**
     * Delete Dishes
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            if(dish != null) {
                redisTemplate.delete("dish_" + dish.getCategoryId() + "_1");
                dishService.deleteByIdWithFlavor(id);
            }
        }
        return R.success("delete success");
    }

    /**
     * List Dishes (Used by Mobile/User side)
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishList(Dish dish){
        List<DishDto> dishDtos = null;

        // Construct Redis Key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        // Check Redis
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtos != null){
            return R.success(dishDtos);
        }

        // Query Database if Cache Miss
        List<Dish> list = dishService.list(dish.getCategoryId(), dish.getStatus());

        dishDtos = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            // Set Flavors
            List<DishFlavor> flavors = dishFlavorService.findByDishId(item.getId());
            dishDto.setFlavors(flavors);

            return dishDto;
        }).collect(Collectors.toList());

        // Save to Redis
        redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);

        return R.success(dishDtos);
    }
}
