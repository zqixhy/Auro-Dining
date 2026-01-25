package com.qiao.service;

import com.qiao.entity.User;

public interface UserService {

    // Find user by phone for login
    User getByPhone(String phone);

    // Save or update user
    User save(User user);

    // 🔥 Add this: Find user by ID for order processing
    User getById(Long id);
}