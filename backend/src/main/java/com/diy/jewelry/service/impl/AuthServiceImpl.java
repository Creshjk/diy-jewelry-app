package com.diy.jewelry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diy.jewelry.common.BusinessException;
import com.diy.jewelry.dto.AdminRegisterRequest;
import com.diy.jewelry.dto.LoginRequest;
import com.diy.jewelry.dto.RegisterRequest;
import com.diy.jewelry.dto.UserProfileUpdateRequest;
import com.diy.jewelry.entity.AdminUser;
import com.diy.jewelry.entity.LoginLog;
import com.diy.jewelry.entity.User;
import com.diy.jewelry.mapper.AdminUserMapper;
import com.diy.jewelry.mapper.LoginLogMapper;
import com.diy.jewelry.mapper.UserMapper;
import com.diy.jewelry.service.AuthService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final AdminUserMapper adminUserMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    public Map<String, Object> register(RegisterRequest request) {
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
        user.setGender(StringUtils.hasText(request.getGender()) ? request.getGender() : "保密");
        user.setAvatar("https://api.dicebear.com/7.x/notionists/svg?seed=" + request.getUsername());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        return result;
    }

    @Override
    public Map<String, Object> registerAdmin(AdminRegisterRequest request) {
        long count = adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BusinessException("管理员用户名已存在");
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(request.getUsername());
        adminUser.setPassword(request.getPassword());
        adminUser.setDisplayName(request.getDisplayName());
        adminUser.setRole("SUPER_ADMIN");
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUserMapper.insert(adminUser);

        Map<String, Object> result = new HashMap<>();
        result.put("id", adminUser.getId());
        result.put("username", adminUser.getUsername());
        result.put("displayName", adminUser.getDisplayName());
        result.put("role", adminUser.getRole());
        return result;
    }

    @Override
    public Map<String, Object> login(LoginRequest request, String ipAddress) {
        if ("admin".equalsIgnoreCase(request.getUserType())) {
            AdminUser admin = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                    .eq(AdminUser::getUsername, request.getUsername())
                    .eq(AdminUser::getPassword, request.getPassword()));
            if (admin == null) {
                throw new BusinessException("管理员账号或密码错误");
            }
            saveLog("admin", admin.getId(), admin.getUsername(), ipAddress);
            Map<String, Object> result = new HashMap<>();
            result.put("id", admin.getId());
            result.put("username", admin.getUsername());
            result.put("displayName", admin.getDisplayName());
            result.put("role", admin.getRole());
            return result;
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .eq(User::getPassword, request.getPassword()));
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        saveLog("user", user.getId(), user.getUsername(), ipAddress);
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        return result;
    }

    @Override
    public Map<String, Object> getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return buildUserProfile(user);
    }

    @Override
    public Map<String, Object> updateUserProfile(UserProfileUpdateRequest request) {
        User user = userMapper.selectById(request.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        if (StringUtils.hasText(request.getGender())) {
            user.setGender(request.getGender());
        }
        user.setPhone(request.getPhone());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return buildUserProfile(user);
    }

    private void saveLog(String userType, Long userId, String username, String ipAddress) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserType(userType);
        loginLog.setUserId(userId);
        loginLog.setUsername(username);
        loginLog.setLoginIp(ipAddress);
        loginLog.setCreatedAt(LocalDateTime.now());
        loginLogMapper.insert(loginLog);
    }

    private Map<String, Object> buildUserProfile(User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("gender", user.getGender());
        result.put("phone", user.getPhone());
        result.put("createdAt", user.getCreatedAt());
        result.put("updatedAt", user.getUpdatedAt());
        return result;
    }
}
