package com.diy.jewelry.vo;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class DashboardVO {

    private long userCount;
    private long designCount;
    private long postCount;
    private long likeCount;
    private List<Map<String, Object>> recentDesigns;
    private List<Map<String, Object>> hotPosts;
}
