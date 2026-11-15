package com.diy.jewelry.controller;

import com.diy.jewelry.common.ApiResponse;
import com.diy.jewelry.dto.GenerateDesignRequest;
import com.diy.jewelry.entity.DesignRecord;
import com.diy.jewelry.entity.PromptRecommendation;
import com.diy.jewelry.service.DesignService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/design")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;

    @GetMapping("/recommendations")
    public ApiResponse<List<PromptRecommendation>> listRecommendations() {
        return ApiResponse.success(designService.listRecommendations());
    }

    @PostMapping("/generate")
    public ApiResponse<DesignRecord> generate(@Valid @RequestBody GenerateDesignRequest request) {
        return ApiResponse.success(designService.generateDesign(request));
    }

    @GetMapping("/history")
    public ApiResponse<List<Map<String, Object>>> listHistory(@RequestParam Long userId) {
        return ApiResponse.success(designService.listUserDesigns(userId));
    }

    @GetMapping("/history/{recordId}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long recordId) {
        return ApiResponse.success(designService.getDesignDetail(recordId));
    }

    @DeleteMapping("/history/{recordId}")
    public ApiResponse<Void> delete(@PathVariable Long recordId) {
        designService.deleteDesign(recordId);
        return ApiResponse.success(null);
    }
}
