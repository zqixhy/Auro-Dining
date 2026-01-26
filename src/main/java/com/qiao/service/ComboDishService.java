package com.qiao.service;

import com.qiao.entity.ComboDish;
import java.util.List;

public interface ComboDishService {
    void saveBatch(List<ComboDish> comboDishes);
}
