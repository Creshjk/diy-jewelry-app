package com.diy.jewelry.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diy.jewelry.common.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diy.jewelry.common.BusinessException;
import com.diy.jewelry.dto.UserSaveRequest;
import com.diy.jewelry.entity.DesignRecord;
import com.diy.jewelry.entity.GalleryPost;
import com.diy.jewelry.entity.User;
import com.diy.jewelry.mapper.DesignRecordMapper;
import com.diy.jewelry.mapper.GalleryLikeMapper;
import com.diy.jewelry.mapper.GalleryPostMapper;
import com.diy.jewelry.mapper.UserMapper;
import com.diy.jewelry.service.AdminService;
import com.diy.jewelry.vo.DashboardVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final DesignRecordMapper designRecordMapper;
    private final GalleryPostMapper galleryPostMapper;
    private final GalleryLikeMapper galleryLikeMapper;

    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setUserCount(userMapper.selectCount(null));
        vo.setDesignCount(designRecordMapper.selectCount(null));
        vo.setPostCount(galleryPostMapper.selectCount(null));
        vo.setLikeCount(galleryLikeMapper.selectCount(null));
        return vo;
    }

    @Override
    public PageResponse<User> listUsers(long current, long size) {
        Page<User> page = userMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt));
        return PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    @Override
    public User createUser(UserSaveRequest request) {
        long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setAvatar("https://api.dicebear.com/7.x/notionists/svg?seed=" + request.getUsername());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public PageResponse<Map<String, Object>> listDesignRecords(long current, long size) {
        Page<DesignRecord> page = designRecordMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<DesignRecord>().orderByDesc(DesignRecord::getCreatedAt));
        List<Map<String, Object>> result = new ArrayList<>();
        for (DesignRecord record : page.getRecords()) {
            User user = userMapper.selectById(record.getUserId());
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("nickname", user == null ? "未知用户" : user.getNickname());
            item.put("prompt", record.getPrompt());
            item.put("designMode", record.getDesignMode());
            item.put("clarityLevel", record.getClarityLevel());
            item.put("aspectRatio", record.getAspectRatio());
            item.put("resultImageUrl", record.getResultImageUrl());
            item.put("createdAt", record.getCreatedAt());
            result.add(item);
        }
        return PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), result);
    }

    @Override
    public PageResponse<Map<String, Object>> listPosts(long current, long size) {
        Page<GalleryPost> page = galleryPostMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<GalleryPost>().orderByDesc(GalleryPost::getCreatedAt));
        List<Map<String, Object>> result = new ArrayList<>();
        for (GalleryPost post : page.getRecords()) {
            User user = userMapper.selectById(post.getUserId());
            Map<String, Object> item = new HashMap<>();
            item.put("id", post.getId());
            item.put("title", post.getTitle());
            item.put("designRecordId", post.getDesignRecordId());
            item.put("userId", post.getUserId());
            item.put("nickname", user == null ? "未知用户" : user.getNickname());
            item.put("avatar", user == null ? null : user.getAvatar());
            item.put("coverImageUrl", post.getCoverImageUrl());
            item.put("description", post.getDescription());
            item.put("likeCount", post.getLikeCount());
            item.put("createdAt", post.getCreatedAt());
            result.add(item);
        }
        return PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), result);
    }

    @Override
    public Map<String, Object> getPostDetail(Long postId) {
        GalleryPost post = galleryPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("作品不存在");
        }
        User user = userMapper.selectById(post.getUserId());
        DesignRecord designRecord = designRecordMapper.selectById(post.getDesignRecordId());
        Map<String, Object> result = new HashMap<>();
        result.put("id", post.getId());
        result.put("title", post.getTitle());
        result.put("description", post.getDescription());
        result.put("coverImageUrl", post.getCoverImageUrl());
        result.put("likeCount", post.getLikeCount());
        result.put("status", post.getStatus());
        result.put("createdAt", post.getCreatedAt());
        result.put("updatedAt", post.getUpdatedAt());
        result.put("designRecordId", post.getDesignRecordId());
        result.put("userId", post.getUserId());
        result.put("publisherName", user == null ? "未知用户" : user.getNickname());
        result.put("publisherUsername", user == null ? "" : user.getUsername());
        result.put("publisherAvatar", user == null ? null : user.getAvatar());
        if (designRecord != null) {
            result.put("prompt", designRecord.getPrompt());
            result.put("styleTags", designRecord.getStyleTags());
            result.put("designMode", designRecord.getDesignMode());
            result.put("clarityLevel", designRecord.getClarityLevel());
            result.put("aspectRatio", designRecord.getAspectRatio());
            result.put("referenceImageUrl", designRecord.getReferenceImageUrl());
            result.put("resultImageUrl", designRecord.getResultImageUrl());
            result.put("aiSummary", designRecord.getAiSummary());
        }
        return result;
    }

    @Override
    public PageResponse<Map<String, Object>> listRecentDesigns(long current, long size) {
        Page<DesignRecord> page = designRecordMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<DesignRecord>().orderByDesc(DesignRecord::getCreatedAt));
        List<Map<String, Object>> result = new ArrayList<>();
        for (DesignRecord record : page.getRecords()) {
            User user = userMapper.selectById(record.getUserId());
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("nickname", user == null ? "未知用户" : user.getNickname());
            item.put("prompt", record.getPrompt());
            item.put("createdAt", record.getCreatedAt());
            result.add(item);
        }
        return PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), result);
    }

    @Override
    public PageResponse<Map<String, Object>> listHotPosts(long current, long size) {
        Page<GalleryPost> page = galleryPostMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<GalleryPost>().orderByDesc(GalleryPost::getLikeCount));
        List<Map<String, Object>> result = new ArrayList<>();
        for (GalleryPost post : page.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", post.getId());
            item.put("title", post.getTitle());
            item.put("likeCount", post.getLikeCount());
            item.put("createdAt", post.getCreatedAt());
            result.add(item);
        }
        return PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), result);
    }
}
