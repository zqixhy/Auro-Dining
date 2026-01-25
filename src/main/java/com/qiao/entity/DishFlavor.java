package com.qiao.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Dish Flavor Entity
 */
@Data
@Entity
@Table(name = "dish_flavor")
public class DishFlavor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dish ID (Foreign Key logic)
    private Long dishId;

    // Flavor Name (e.g., "Spiciness")
    private String name;

    // Flavor Value (e.g., "Medium Spicy")
    private String value;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

    private Integer isDeleted;
}