package com.qiao.dto;

import com.qiao.entity.Combo;
import com.qiao.entity.ComboDish;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ComboDto extends Combo {
    private String categoryName;

    private List<ComboDish> comboDishes = new ArrayList<>();

    private Integer copies;
}
