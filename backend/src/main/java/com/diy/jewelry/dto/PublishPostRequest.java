package com.diy.jewelry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PublishPostRequest {

    @NotNull(message = "不能为空")
    private Long userId;

    @NotNull(message = "不能为空")
    private Long designRecordId;

    @NotBlank(message = "不能为空")
    private String title;

    private String description;
}
