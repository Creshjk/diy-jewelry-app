package com.diy.jewelry.service;

import com.diy.jewelry.common.PageResponse;
import com.diy.jewelry.dto.UserSaveRequest;
import com.diy.jewelry.entity.User;
import com.diy.jewelry.vo.DashboardVO;
import java.util.Map;

public interface AdminService {

    DashboardVO getDashboard();

    PageResponse<User> listUsers(long current, long size);

    User createUser(UserSaveRequest request);

    void deleteUser(Long userId);

    PageResponse<Map<String, Object>> listDesignRecords(long current, long size);

    PageResponse<Map<String, Object>> listPosts(long current, long size);

    Map<String, Object> getPostDetail(Long postId);

    PageResponse<Map<String, Object>> listRecentDesigns(long current, long size);

    PageResponse<Map<String, Object>> listHotPosts(long current, long size);
}
