package com.qiao.repository;

import com.qiao.entity.SetmealDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetmealDishRepository extends JpaRepository<SetmealDish, Long> {

    List<SetmealDish> findBySetmealId(String setmealId);

    void deleteBySetmealId(String setmealId);

    void deleteBySetmealIdIn(List<String> setmealIds);
}