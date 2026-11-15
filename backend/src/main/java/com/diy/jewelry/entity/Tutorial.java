package com.diy.jewelry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("tutorial")
public class Tutorial {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String coverImageUrl;
    private String summary;
    private String linkUrl;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
