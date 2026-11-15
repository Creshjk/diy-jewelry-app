package com.diy.jewelry.controller;

import com.diy.jewelry.common.ApiResponse;
import com.diy.jewelry.dto.PublishPostRequest;
import com.diy.jewelry.service.DiscoveryService;
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
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    @GetMapping("/posts")
    public ApiResponse<List<Map<String, Object>>> listPosts(@RequestParam(required = false) Long userId) {
        return ApiResponse.success(discoveryService.listPosts(userId));
    }

    @PostMapping("/posts")
    public ApiResponse<Map<String, Object>> publish(@Valid @RequestBody PublishPostRequest request) {
        return ApiResponse.success(discoveryService.publishPost(request));
    }

    @PostMapping("/posts/{postId}/like")
    public ApiResponse<Map<String, Object>> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        return ApiResponse.success(discoveryService.toggleLike(postId, userId));
    }

    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> delete(@PathVariable Long postId) {
        discoveryService.deletePost(postId);
        return ApiResponse.success(null);
    }
}
