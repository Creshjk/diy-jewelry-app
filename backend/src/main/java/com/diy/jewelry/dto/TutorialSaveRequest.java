package com.diy.jewelry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TutorialSaveRequest {

    private Long id;

    @NotBlank(message = "不能为空")
    private String title;

    private String coverImageUrl;
    private String summary;
    private String linkUrl;
    private String content;
    private Integer status = 1;
}
