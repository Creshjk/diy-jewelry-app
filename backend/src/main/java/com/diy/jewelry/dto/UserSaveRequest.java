package com.diy.jewelry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSaveRequest {

    @NotBlank(message = "不能为空")
    private String username;

    @NotBlank(message = "不能为空")
    private String password;

    @NotBlank(message = "不能为空")
    private String nickname;

    private String phone;
    private String gender;
}
