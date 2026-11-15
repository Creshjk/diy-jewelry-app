package com.diy.jewelry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("gallery_post")
public class GalleryPost {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long designRecordId;
    private String title;
    private String coverImageUrl;
    private String description;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
