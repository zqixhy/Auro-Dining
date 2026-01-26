package com.qiao.controller;

import com.qiao.common.R;
import com.qiao.entity.User;
import com.qiao.service.UserService;
import com.qiao.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Controller for User Frontend - User Authentication
 * Handles user login, logout, and verification code sending for mobile clients
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Send verification code via phone
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        String phone = user.getPhone();
        if(phone != null){
            // Generate 4-digit code
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("Verification code for {}: {}", phone, code);

            // Store code in Redis for 5 minutes
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("Send success");
        }
        return R.error("Send failed");
    }

    /**
     * Mobile User Login
     */
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody Map map){
        Object phoneObj = map.get("phone");
        Object codeObj = map.get("code");
        
        if(phoneObj == null || codeObj == null){
            return R.error("Login failed: Phone or code is missing");
        }
        
        String phone = phoneObj.toString();
        String code = codeObj.toString();

        log.info("Attempting login: phone={}, code={}", phone, code);

        // 1. Verify code from Redis
        Object cachedCode = redisTemplate.opsForValue().get(phone);
        if(cachedCode != null && cachedCode.equals(code)){

            // 2. Check if user exists in database
            User user = userService.getByPhone(phone);

            // 3. Auto-register if user is new
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1); // Default status: enabled
                userService.save(user);
            }

            // 4. Store user ID in session
            request.getSession().setAttribute("user", user.getId());

            // 5. Clear Redis code after successful login
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("Login failed: Invalid code");
    }

    /**
     * Mobile User Logout
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request) {
        // 1. Remove user ID from session
        request.getSession().removeAttribute("user");
        // 2. Clear current thread local user context
        com.qiao.common.BaseContext.removeCurrentId();
        log.info("User logged out successfully");
        return R.success("Logout successful");
    }
}