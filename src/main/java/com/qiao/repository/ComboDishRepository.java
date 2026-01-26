package com.qiao.repository;

import com.qiao.entity.ComboDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboDishRepository extends JpaRepository<ComboDish, Long> {

    List<ComboDish> findByComboId(String comboId);

    void deleteByComboId(String comboId);

    void deleteByComboIdIn(List<String> comboIds);
}
