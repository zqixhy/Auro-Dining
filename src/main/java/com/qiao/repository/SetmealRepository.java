package com.qiao.repository;

import com.qiao.entity.Setmeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetmealRepository extends JpaRepository<Setmeal, Long> {

    Page<Setmeal> findByNameContaining(String name, Pageable pageable);

    List<Setmeal> findByCategoryId(Long categoryId);

    int countByCategoryId(Long categoryId);
}