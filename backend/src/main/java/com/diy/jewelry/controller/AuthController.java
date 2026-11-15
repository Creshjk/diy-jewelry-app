package com.diy.jewelry.controller;

import com.diy.jewelry.common.ApiResponse;
import com.diy.jewelry.dto.AdminRegisterRequest;
import com.diy.jewelry.dto.LoginRequest;
import com.diy.jewelry.dto.RegisterRequest;
import com.diy.jewelry.dto.UserProfileUpdateRequest;
import com.diy.jewelry.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/admin/register")
    public ApiResponse<Map<String, Object>> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        return ApiResponse.success(authService.registerAdmin(request));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request,
                                                  HttpServletRequest servletRequest) {
        return ApiResponse.success(authService.login(request, servletRequest.getRemoteAddr()));
    }

    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> profile(@RequestParam Long userId) {
        return ApiResponse.success(authService.getUserProfile(userId));
    }

    @PutMapping("/profile")
    public ApiResponse<Map<String, Object>> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        return ApiResponse.success(authService.updateUserProfile(request));
    }
}
