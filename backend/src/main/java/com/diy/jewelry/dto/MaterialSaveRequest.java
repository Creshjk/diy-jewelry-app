package com.diy.jewelry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MaterialSaveRequest {

    private Long id;

    @NotBlank(message = "不能为空")
    private String bodyPart;

    @NotBlank(message = "不能为空")
    private String materialName;

    private String coverImageUrl;
    private String description;
    private String linkUrl;
    private Integer status = 1;
}
