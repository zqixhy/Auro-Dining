package com.qiao.controller;

import com.qiao.common.R;
import com.qiao.entity.Category;
import com.qiao.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Map<String, Object>> getPage(int page, int pageSize) {
        Page<Category> pageInfo = categoryService.page(page, pageSize);

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("records", pageInfo.getContent());
        pageData.put("total", pageInfo.getTotalElements());

        return R.success(pageData);
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Category category){
        log.info("add new category: {}", category);

        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        category.setCreateUser(empId);
        category.setUpdateUser(empId);

        categoryService.save(category);
        return R.success("add success");
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Category category){
        log.info("update category: {}", category);

        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        categoryService.update(category);
        return R.success("update success");
    }

    @GetMapping("/{id}")
    public R<Category> getById(@PathVariable Long id){
        Category category = categoryService.getById(id);
        if(category != null){
            return R.success(category);
        }
        return R.error("category not found");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){ // Note: parameter name is usually 'ids'
        categoryService.remove(ids);
        return R.success("delete success");
    }

    @GetMapping("/list")
    public R<List<Category>> getList(Category category){
        List<Category> list = categoryService.list(category);
        return R.success(list);
    }
}