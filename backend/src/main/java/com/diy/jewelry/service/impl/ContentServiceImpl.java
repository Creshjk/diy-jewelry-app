package com.diy.jewelry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diy.jewelry.dto.MaterialSaveRequest;
import com.diy.jewelry.dto.TutorialSaveRequest;
import com.diy.jewelry.entity.MaterialRecommendation;
import com.diy.jewelry.entity.Tutorial;
import com.diy.jewelry.mapper.MaterialRecommendationMapper;
import com.diy.jewelry.mapper.TutorialMapper;
import com.diy.jewelry.service.ContentService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final TutorialMapper tutorialMapper;
    private final MaterialRecommendationMapper materialRecommendationMapper;

    @Override
    public List<Tutorial> listTutorials() {
        return tutorialMapper.selectList(new LambdaQueryWrapper<Tutorial>().orderByDesc(Tutorial::getCreatedAt));
    }

    @Override
    public List<MaterialRecommendation> listMaterials() {
        return materialRecommendationMapper.selectList(new LambdaQueryWrapper<MaterialRecommendation>()
                .orderByAsc(MaterialRecommendation::getBodyPart)
                .orderByDesc(MaterialRecommendation::getCreatedAt));
    }

    @Override
    public Tutorial saveTutorial(TutorialSaveRequest request) {
        Tutorial tutorial = request.getId() == null ? new Tutorial() : tutorialMapper.selectById(request.getId());
        BeanUtils.copyProperties(request, tutorial);
        if (tutorial.getId() == null) {
            tutorial.setCreatedAt(LocalDateTime.now());
        }
        tutorial.setUpdatedAt(LocalDateTime.now());
        if (request.getId() == null) {
            tutorialMapper.insert(tutorial);
        } else {
            tutorialMapper.updateById(tutorial);
        }
        return tutorial;
    }

    @Override
    public MaterialRecommendation saveMaterial(MaterialSaveRequest request) {
        MaterialRecommendation material = request.getId() == null
                ? new MaterialRecommendation()
                : materialRecommendationMapper.selectById(request.getId());
        BeanUtils.copyProperties(request, material);
        if (material.getId() == null) {
            material.setCreatedAt(LocalDateTime.now());
        }
        material.setUpdatedAt(LocalDateTime.now());
        if (request.getId() == null) {
            materialRecommendationMapper.insert(material);
        } else {
            materialRecommendationMapper.updateById(material);
        }
        return material;
    }

    @Override
    public void deleteTutorial(Long id) {
        tutorialMapper.deleteById(id);
    }

    @Override
    public void deleteMaterial(Long id) {
        materialRecommendationMapper.deleteById(id);
    }
}
