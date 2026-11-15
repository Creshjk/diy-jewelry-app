package com.diy.jewelry.service;

import com.diy.jewelry.dto.PublishPostRequest;
import java.util.List;
import java.util.Map;

public interface DiscoveryService {

    List<Map<String, Object>> listPosts(Long currentUserId);

    Map<String, Object> publishPost(PublishPostRequest request);

    Map<String, Object> toggleLike(Long postId, Long userId);

    void deletePost(Long postId);
}
