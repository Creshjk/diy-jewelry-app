package com.diy.jewelry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerateDesignRequest {

    @NotNull(message = "不能为空")
    private Long userId;

    @NotBlank(message = "不能为空")
    private String prompt;

    private String styleTags;
    private String clarityLevel;
    private String aspectRatio;
    private String designMode;
    private String referenceImageUrl;
}
