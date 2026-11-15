package com.diy.jewelry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("material_recommendation")
public class MaterialRecommendation {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String bodyPart;
    private String materialName;
    private String coverImageUrl;
    private String description;
    private String linkUrl;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
