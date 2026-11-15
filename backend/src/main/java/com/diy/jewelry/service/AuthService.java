package com.diy.jewelry.service;

import com.diy.jewelry.dto.AdminRegisterRequest;
import com.diy.jewelry.dto.LoginRequest;
import com.diy.jewelry.dto.RegisterRequest;
import com.diy.jewelry.dto.UserProfileUpdateRequest;
import java.util.Map;

public interface AuthService {

    Map<String, Object> register(RegisterRequest request);

    Map<String, Object> registerAdmin(AdminRegisterRequest request);

    Map<String, Object> login(LoginRequest request, String ipAddress);

    Map<String, Object> getUserProfile(Long userId);

    Map<String, Object> updateUserProfile(UserProfileUpdateRequest request);
}
