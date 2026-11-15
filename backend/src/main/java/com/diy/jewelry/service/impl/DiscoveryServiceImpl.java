package com.diy.jewelry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diy.jewelry.common.BusinessException;
import com.diy.jewelry.dto.PublishPostRequest;
import com.diy.jewelry.entity.DesignRecord;
import com.diy.jewelry.entity.GalleryLike;
import com.diy.jewelry.entity.GalleryPost;
import com.diy.jewelry.entity.OperationLog;
import com.diy.jewelry.entity.User;
import com.diy.jewelry.mapper.DesignRecordMapper;
import com.diy.jewelry.mapper.GalleryLikeMapper;
import com.diy.jewelry.mapper.GalleryPostMapper;
import com.diy.jewelry.mapper.OperationLogMapper;
import com.diy.jewelry.mapper.UserMapper;
import com.diy.jewelry.service.DiscoveryService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {

    private final GalleryPostMapper galleryPostMapper;
    private final GalleryLikeMapper galleryLikeMapper;
    private final DesignRecordMapper designRecordMapper;
    private final UserMapper userMapper;
    private final OperationLogMapper operationLogMapper;

    @Override
    public List<Map<String, Object>> listPosts(Long currentUserId) {
        List<GalleryPost> posts = galleryPostMapper.selectList(new LambdaQueryWrapper<GalleryPost>()
                .eq(GalleryPost::getStatus, 1)
                .orderByDesc(GalleryPost::getCreatedAt));
        List<Map<String, Object>> result = new ArrayList<>();
        for (GalleryPost post : posts) {
            User user = userMapper.selectById(post.getUserId());
            boolean liked = currentUserId != null && galleryLikeMapper.selectCount(new LambdaQueryWrapper<GalleryLike>()
                    .eq(GalleryLike::getPostId, post.getId())
                    .eq(GalleryLike::getUserId, currentUserId)) > 0;
            Map<String, Object> item = new HashMap<>();
            item.put("id", post.getId());
            item.put("title", post.getTitle());
            item.put("coverImageUrl", post.getCoverImageUrl());
            item.put("description", post.getDescription());
            item.put("likeCount", post.getLikeCount());
            item.put("createdAt", post.getCreatedAt());
            item.put("liked", liked);
            item.put("nickname", user == null ? "匿名用户" : user.getNickname());
            item.put("avatar", user == null ? null : user.getAvatar());
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> publishPost(PublishPostRequest request) {
        DesignRecord record = designRecordMapper.selectById(request.getDesignRecordId());
        if (record == null) {
            throw new BusinessException("设计记录不存在");
        }
        if (!record.getUserId().equals(request.getUserId())) {
            throw new BusinessException("只能发布自己的作品");
        }

        GalleryPost post = new GalleryPost();
        post.setUserId(request.getUserId());
        post.setDesignRecordId(record.getId());
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setCoverImageUrl(record.getResultImageUrl());
        post.setLikeCount(0);
        post.setStatus(1);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        galleryPostMapper.insert(post);

        record.setPublished(1);
        record.setUpdatedAt(LocalDateTime.now());
        designRecordMapper.updateById(record);

        saveLog("发布作品", post.getId(), "用户发布了一条作品到发现广场");
        Map<String, Object> result = new HashMap<>();
        result.put("postId", post.getId());
        result.put("title", post.getTitle());
        return result;
    }

    @Override
    public Map<String, Object> toggleLike(Long postId, Long userId) {
        GalleryPost post = galleryPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("作品不存在");
        }

        GalleryLike existed = galleryLikeMapper.selectOne(new LambdaQueryWrapper<GalleryLike>()
                .eq(GalleryLike::getPostId, postId)
                .eq(GalleryLike::getUserId, userId));

        boolean liked;
        if (existed == null) {
            GalleryLike like = new GalleryLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            galleryLikeMapper.insert(like);
            post.setLikeCount(post.getLikeCount() + 1);
            liked = true;
        } else {
            galleryLikeMapper.deleteById(existed.getId());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            liked = false;
        }
        post.setUpdatedAt(LocalDateTime.now());
        galleryPostMapper.updateById(post);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", post.getLikeCount());
        return result;
    }

    @Override
    public void deletePost(Long postId) {
        galleryPostMapper.deleteById(postId);
        galleryLikeMapper.delete(new LambdaQueryWrapper<GalleryLike>().eq(GalleryLike::getPostId, postId));
        saveLog("删除作品", postId, "删除了一条广场作品");
    }

    private void saveLog(String actionType, Long targetId, String content) {
        OperationLog log = new OperationLog();
        log.setOperatorType("admin");
        log.setOperatorId(1L);
        log.setActionType(actionType);
        log.setTargetType("gallery_post");
        log.setTargetId(targetId);
        log.setContent(content);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
