package com.diy.jewelry.controller;

import com.diy.jewelry.common.ApiResponse;
import com.diy.jewelry.dto.MaterialSaveRequest;
import com.diy.jewelry.dto.TutorialSaveRequest;
import com.diy.jewelry.entity.MaterialRecommendation;
import com.diy.jewelry.entity.Tutorial;
import com.diy.jewelry.service.ContentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/tutorials")
    public ApiResponse<List<Tutorial>> tutorials() {
        return ApiResponse.success(contentService.listTutorials());
    }

    @GetMapping("/materials")
    public ApiResponse<List<MaterialRecommendation>> materials() {
        return ApiResponse.success(contentService.listMaterials());
    }

    @PostMapping("/tutorials")
    public ApiResponse<Tutorial> saveTutorial(@Valid @RequestBody TutorialSaveRequest request) {
        return ApiResponse.success(contentService.saveTutorial(request));
    }

    @PostMapping("/materials")
    public ApiResponse<MaterialRecommendation> saveMaterial(@Valid @RequestBody MaterialSaveRequest request) {
        return ApiResponse.success(contentService.saveMaterial(request));
    }

    @DeleteMapping("/tutorials/{id}")
    public ApiResponse<Void> deleteTutorial(@PathVariable Long id) {
        contentService.deleteTutorial(id);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/materials/{id}")
    public ApiResponse<Void> deleteMaterial(@PathVariable Long id) {
        contentService.deleteMaterial(id);
        return ApiResponse.success(null);
    }
}
