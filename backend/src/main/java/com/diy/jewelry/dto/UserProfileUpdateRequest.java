package com.diy.jewelry.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {

    @NotNull(message = "不能为空")
    private Long id;

    private String nickname;
    private String avatar;
    private String gender;
    private String phone;
}
