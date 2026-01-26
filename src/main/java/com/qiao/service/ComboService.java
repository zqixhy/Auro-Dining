package com.qiao.service;

import com.qiao.dto.ComboDto;
import com.qiao.entity.Combo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ComboService {

    Combo getById(Long id);
    void update(Combo combo);
    List<Combo> list(Combo combo);

    Page<Combo> page(int page, int pageSize, String name);

    void saveCombo(ComboDto comboDto);

    ComboDto getByIdWithDish(Long id);

    void updateWithDish(ComboDto comboDto);

    void deleteWithDish(List<Long> ids);
}
