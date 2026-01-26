package com.qiao.service.impl;

import com.qiao.entity.ComboDish;
import com.qiao.repository.ComboDishRepository;
import com.qiao.service.ComboDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComboDishServiceImpl implements ComboDishService {

    @Autowired
    private ComboDishRepository comboDishRepository;

    @Override
    public void saveBatch(List<ComboDish> comboDishes) {
        comboDishRepository.saveAll(comboDishes);
    }
}
