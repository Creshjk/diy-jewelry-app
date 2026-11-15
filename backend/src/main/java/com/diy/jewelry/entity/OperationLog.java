package com.diy.jewelry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String operatorType;
    private Long operatorId;
    private String actionType;
    private String targetType;
    private Long targetId;
    private String content;
    private LocalDateTime createdAt;
}
