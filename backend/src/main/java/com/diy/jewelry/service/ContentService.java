package com.diy.jewelry.service;

import com.diy.jewelry.dto.MaterialSaveRequest;
import com.diy.jewelry.dto.TutorialSaveRequest;
import com.diy.jewelry.entity.MaterialRecommendation;
import com.diy.jewelry.entity.Tutorial;
import java.util.List;

public interface ContentService {

    List<Tutorial> listTutorials();

    List<MaterialRecommendation> listMaterials();

    Tutorial saveTutorial(TutorialSaveRequest request);

    MaterialRecommendation saveMaterial(MaterialSaveRequest request);

    void deleteTutorial(Long id);

    void deleteMaterial(Long id);
}
