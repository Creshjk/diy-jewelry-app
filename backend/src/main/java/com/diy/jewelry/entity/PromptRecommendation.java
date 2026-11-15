package com.diy.jewelry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("ai_prompt_recommendation")
public class PromptRecommendation {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String promptText;
    private String category;
    private Integer sortOrder;
    private Integer enabled;
    private LocalDateTime createdAt;
}
