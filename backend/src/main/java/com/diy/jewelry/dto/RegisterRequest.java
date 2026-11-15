package com.diy.jewelry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "不能为空")
    private String username;

    @NotBlank(message = "不能为空")
    private String password;

    @NotBlank(message = "不能为空")
    private String nickname;

    private String gender;
    private String phone;
}
