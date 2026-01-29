package com.qiao.service;

import com.qiao.entity.User;

public interface UserService {


    // Find user by email for login identity
    User getByEmail(String email);

    // Save or update user
    User save(User user);

    // 🔥 Add this: Find user by ID for order processing
    User getById(Long id);
}