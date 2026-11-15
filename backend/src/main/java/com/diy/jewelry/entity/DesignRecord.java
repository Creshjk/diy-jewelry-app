package com.diy.jewelry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("ai_design_record")
public class DesignRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String designMode;
    private String prompt;
    private String styleTags;
    private String clarityLevel;
    private String aspectRatio;
    private String referenceImageUrl;
    private String resultImageUrl;
    private String aiSummary;
    private Integer published;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
