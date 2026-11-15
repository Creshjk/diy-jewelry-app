package com.diy.jewelry.service;

import com.diy.jewelry.dto.GenerateDesignRequest;
import com.diy.jewelry.entity.DesignRecord;
import com.diy.jewelry.entity.PromptRecommendation;
import java.util.List;
import java.util.Map;

public interface DesignService {

    List<PromptRecommendation> listRecommendations();

    DesignRecord generateDesign(GenerateDesignRequest request);

    List<Map<String, Object>> listUserDesigns(Long userId);

    Map<String, Object> getDesignDetail(Long recordId);

    void deleteDesign(Long recordId);
}
