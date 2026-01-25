package com.qiao.repository;

import com.qiao.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByTypeOrderBySortAscUpdateTimeDesc(Integer type);

    List<Category> findAllByOrderBySortAscUpdateTimeDesc();
}