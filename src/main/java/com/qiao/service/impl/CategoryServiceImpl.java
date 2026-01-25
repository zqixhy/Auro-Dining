package com.qiao.service.impl;

import com.qiao.common.CustomException;
import com.qiao.entity.Category;
import com.qiao.repository.CategoryRepository;
import com.qiao.repository.DishRepository;
import com.qiao.repository.SetmealRepository;
import com.qiao.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private SetmealRepository setmealRepository;

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Delete category with checks
     */
    @Override
    public void remove(Long id) {
        // Check if associated with dishes
        int dishCount = dishRepository.countByCategoryId(id);
        if (dishCount > 0) {
            throw new CustomException("Category is associated with dishes, cannot delete");
        }

        // Check if associated with setmeals
        int setmealCount = setmealRepository.countByCategoryId(id);
        if (setmealCount > 0) {
            throw new CustomException("Category is associated with setmeals, cannot delete");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public Category update(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Category> page(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by("sort").ascending());
        return categoryRepository.findAll(pageRequest);
    }

    @Override
    public List<Category> list(Category category) {
        if (category.getType() != null) {
            return categoryRepository.findByTypeOrderBySortAscUpdateTimeDesc(category.getType());
        }
        return categoryRepository.findAllByOrderBySortAscUpdateTimeDesc();
    }
}