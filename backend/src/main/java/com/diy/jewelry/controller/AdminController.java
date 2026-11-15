package com.diy.jewelry.controller;

import com.diy.jewelry.common.ApiResponse;
import com.diy.jewelry.common.PageResponse;
import com.diy.jewelry.dto.UserSaveRequest;
import com.diy.jewelry.entity.User;
import com.diy.jewelry.service.AdminService;
import com.diy.jewelry.vo.DashboardVO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardVO> dashboard() {
        return ApiResponse.success(adminService.getDashboard());
    }

    @GetMapping("/dashboard/recent-designs")
    public ApiResponse<PageResponse<Map<String, Object>>> recentDesigns(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "5") long size) {
        return ApiResponse.success(adminService.listRecentDesigns(current, size));
    }

    @GetMapping("/dashboard/hot-posts")
    public ApiResponse<PageResponse<Map<String, Object>>> hotPosts(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "5") long size) {
        return ApiResponse.success(adminService.listHotPosts(current, size));
    }

    @GetMapping("/users")
    public ApiResponse<PageResponse<User>> users(@RequestParam(defaultValue = "1") long current,
                                                 @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(adminService.listUsers(current, size));
    }

    @PostMapping("/users")
    public ApiResponse<User> createUser(@Valid @RequestBody UserSaveRequest request) {
        return ApiResponse.success(adminService.createUser(request));
    }

    @DeleteMapping("/users/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ApiResponse.success(null);
    }

    @GetMapping("/design-records")
    public ApiResponse<PageResponse<Map<String, Object>>> designRecords(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(adminService.listDesignRecords(current, size));
    }

    @GetMapping("/posts")
    public ApiResponse<PageResponse<Map<String, Object>>> posts(@RequestParam(defaultValue = "1") long current,
                                                                @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(adminService.listPosts(current, size));
    }

    @GetMapping("/posts/{postId}")
    public ApiResponse<Map<String, Object>> postDetail(@PathVariable Long postId) {
        return ApiResponse.success(adminService.getPostDetail(postId));
    }
}
