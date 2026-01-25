package com.qiao.controller;

import com.qiao.common.R;
import com.qiao.dto.SetmealDto;
import com.qiao.entity.Category;
import com.qiao.entity.Setmeal;
import com.qiao.service.CategoryService;
import com.qiao.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Map<String, Object>> getPage(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = setmealService.page(page, pageSize, name);

        List<SetmealDto> dtoList = pageInfo.getContent().stream().map(item -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item, dto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("records", dtoList);
        pageData.put("total", pageInfo.getTotalElements());

        return R.success(pageData);
    }

    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealDto.setCreateTime(LocalDateTime.now());
        setmealDto.setUpdateTime(LocalDateTime.now());

        setmealService.saveMeal(setmealDto);
        return R.success("save success");
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteWithDish(ids);
        return R.success("delete success");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@RequestParam List<Long> ids, @PathVariable Integer status){
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            if(setmeal != null){
                setmeal.setStatus(status);
                setmeal.setUpdateTime(LocalDateTime.now());
                setmealService.update(setmeal);
            }
        }
        return R.success("change status success");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealDto.setUpdateTime(LocalDateTime.now());
        setmealService.updateWithDish(setmealDto);
        return R.success("update success");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> getList(Setmeal setmeal){
        // Note: Modified cache key to be unique per status
        List<Setmeal> list = setmealService.list(setmeal);
        return R.success(list);
    }
}