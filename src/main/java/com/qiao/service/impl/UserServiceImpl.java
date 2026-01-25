package com.qiao.service.impl;

import com.qiao.entity.User;
import com.qiao.repository.UserRepository;
import com.qiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    // 🔥 Add this implementation
    @Override
    public User getById(Long id) {
        // JPA's findById returns an Optional, so we use .orElse(null)
        return userRepository.findById(id).orElse(null);
    }
}