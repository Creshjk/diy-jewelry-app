package com.diy.jewelry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diy.jewelry.common.BusinessException;
import com.diy.jewelry.config.AiProperties;
import com.diy.jewelry.dto.GenerateDesignRequest;
import com.diy.jewelry.entity.DesignRecord;
import com.diy.jewelry.entity.OperationLog;
import com.diy.jewelry.entity.PromptRecommendation;
import com.diy.jewelry.entity.User;
import com.diy.jewelry.mapper.DesignRecordMapper;
import com.diy.jewelry.mapper.OperationLogMapper;
import com.diy.jewelry.mapper.PromptRecommendationMapper;
import com.diy.jewelry.mapper.UserMapper;
import com.diy.jewelry.service.DesignService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DesignServiceImpl implements DesignService {

    private final PromptRecommendationMapper promptRecommendationMapper;
    private final DesignRecordMapper designRecordMapper;
    private final UserMapper userMapper;
    private final OperationLogMapper operationLogMapper;
    private final AiProperties aiProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public List<PromptRecommendation> listRecommendations() {
        return promptRecommendationMapper.selectList(new LambdaQueryWrapper<PromptRecommendation>()
                .eq(PromptRecommendation::getEnabled, 1)
                .orderByAsc(PromptRecommendation::getSortOrder));
    }

    @Override
    public DesignRecord generateDesign(GenerateDesignRequest request) {
        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        ImageGenerationResult imageResult = generateAiImage(request);

        DesignRecord record = new DesignRecord();
        record.setUserId(request.getUserId());
        record.setDesignMode(StringUtils.hasText(request.getDesignMode()) ? request.getDesignMode() : "TEXT_TO_IMAGE");
        record.setPrompt(request.getPrompt());
        record.setStyleTags(request.getStyleTags());
        record.setClarityLevel(StringUtils.hasText(request.getClarityLevel()) ? request.getClarityLevel() : "高清");
        record.setAspectRatio(StringUtils.hasText(request.getAspectRatio()) ? request.getAspectRatio() : "1:1");
        record.setReferenceImageUrl(request.getReferenceImageUrl());
        record.setResultImageUrl(imageResult.imageUrl());
        record.setAiSummary(callAiSummary(request, imageResult));
        record.setPublished(0);
        record.setStatus(1);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        designRecordMapper.insert(record);

        saveLog("生成作品", "ai_design_record", record.getId(), "用户" + user.getNickname() + "生成了一条首饰设计记录");
        return record;
    }

    @Override
    public List<Map<String, Object>> listUserDesigns(Long userId) {
        List<DesignRecord> records = designRecordMapper.selectList(new LambdaQueryWrapper<DesignRecord>()
                .eq(DesignRecord::getUserId, userId)
                .orderByDesc(DesignRecord::getCreatedAt));
        List<Map<String, Object>> result = new ArrayList<>();
        for (DesignRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("prompt", record.getPrompt());
            item.put("styleTags", record.getStyleTags());
            item.put("clarityLevel", record.getClarityLevel());
            item.put("aspectRatio", record.getAspectRatio());
            item.put("designMode", record.getDesignMode());
            item.put("referenceImageUrl", record.getReferenceImageUrl());
            item.put("resultImageUrl", record.getResultImageUrl());
            item.put("aiSummary", record.getAiSummary());
            item.put("published", record.getPublished());
            item.put("createdAt", record.getCreatedAt());
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> getDesignDetail(Long recordId) {
        DesignRecord record = designRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        User user = userMapper.selectById(record.getUserId());
        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("user", user);
        return result;
    }

    @Override
    public void deleteDesign(Long recordId) {
        designRecordMapper.deleteById(recordId);
        saveLog("删除记录", "ai_design_record", recordId, "删除了一条设计记录");
    }

    private void saveLog(String actionType, String targetType, Long targetId, String content) {
        OperationLog log = new OperationLog();
        log.setOperatorType("system");
        log.setOperatorId(0L);
        log.setActionType(actionType);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setContent(content);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    private ImageGenerationResult generateAiImage(GenerateDesignRequest request) {
        if (!StringUtils.hasText(aiProperties.getImageApiKey())) {
            return new ImageGenerationResult(buildJewelryFallbackImage(request), false, "未配置图像模型密钥");
        }
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", aiProperties.getImageModel());
            payload.put("prompt", buildImagePrompt(request));
            payload.put("response_format", "url");
            payload.put("size", "2K");
            payload.put("stream", false);
            payload.put("watermark", true);
            payload.put("sequential_image_generation", "disabled");
            if (StringUtils.hasText(request.getReferenceImageUrl())) {
                payload.put("image", List.of(request.getReferenceImageUrl()));
            }

            String body = objectMapper.writeValueAsString(payload);
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(aiProperties.getImageEndpoint()))
                    .header("Authorization", "Bearer " + aiProperties.getImageApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode urlNode = root.at("/data/0/url");
            if (response.statusCode() >= 200 && response.statusCode() < 300
                    && !urlNode.isMissingNode() && urlNode.isTextual()) {
                return new ImageGenerationResult(urlNode.asText(), true, null);
            }
            JsonNode errorNode = root.path("error");
            String errorMessage = errorNode.isMissingNode()
                    ? "图像模型返回异常"
                    : errorNode.path("code").asText() + ": " + errorNode.path("message").asText();
            return new ImageGenerationResult(buildJewelryFallbackImage(request), false, errorMessage);
        } catch (Exception ex) {
            return new ImageGenerationResult(buildJewelryFallbackImage(request), false, "图像模型调用失败: " + ex.getMessage());
        }
    }

    private String callAiSummary(GenerateDesignRequest request, ImageGenerationResult imageResult) {
        String baseSummary = buildLocalSummary(request);
        if (!StringUtils.hasText(aiProperties.getApiKey())) {
            return appendImageStatus(baseSummary, imageResult);
        }
        try {
            List<Map<String, Object>> content = new ArrayList<>();
            if (StringUtils.hasText(request.getReferenceImageUrl())) {
                content.add(Map.of("type", "input_image", "image_url", request.getReferenceImageUrl()));
            }
            content.add(Map.of(
                    "type", "input_text",
                    "text", "请根据这些信息输出首饰设计说明：主题=" + request.getPrompt()
                            + "；风格=" + request.getStyleTags()
                            + "；清晰度=" + request.getClarityLevel()
                            + "；比例=" + request.getAspectRatio()
                            + "；模式=" + request.getDesignMode()
            ));

            Map<String, Object> payload = Map.of(
                    "model", aiProperties.getModel(),
                    "input", List.of(Map.of("role", "user", "content", content))
            );

            String body = objectMapper.writeValueAsString(payload);
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(aiProperties.getEndpoint()))
                    .header("Authorization", "Bearer " + aiProperties.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode outputText = root.at("/output/0/content/0/text");
                if (!outputText.isMissingNode() && outputText.isTextual()) {
                    return appendImageStatus(sanitizeSummary(outputText.asText(), baseSummary), imageResult);
                }
            }
        } catch (Exception ignored) {
            return appendImageStatus(baseSummary, imageResult);
        }
        return appendImageStatus(baseSummary, imageResult);
    }

    private String appendImageStatus(String summary, ImageGenerationResult imageResult) {
        if (imageResult.success()) {
            return summary;
        }
        return summary + " 当前展示的是首饰示意图，请稍后重试生成正式设计图。";
    }

    private String buildLocalSummary(GenerateDesignRequest request) {
        StringBuilder builder = new StringBuilder("为用户生成一款");
        builder.append(request.getPrompt()).append("主题的首饰设计方案");
        if (StringUtils.hasText(request.getStyleTags())) {
            builder.append("，整体风格突出").append(request.getStyleTags());
        }
        builder.append("，建议重点表现材质层次、结构细节与佩戴质感，适合用于页面展示和后续手工制作。");
        return builder.toString();
    }

    private String sanitizeSummary(String summary, String fallback) {
        if (!StringUtils.hasText(summary)) {
            return fallback;
        }
        String cleaned = summary.trim();
        String[] markers = new String[]{
                "当前未返回真实设计图",
                "当前展示的是首饰示意图",
                "原因：",
                "原因:",
                "ModelNotOpen",
                "Please activate",
                "Request id"
        };
        int cutIndex = -1;
        for (String marker : markers) {
            int index = cleaned.indexOf(marker);
            if (index >= 0 && (cutIndex < 0 || index < cutIndex)) {
                cutIndex = index;
            }
        }
        if (cutIndex >= 0) {
            cleaned = cleaned.substring(0, cutIndex).trim();
        }
        cleaned = cleaned.replaceAll("\\s+", " ");
        return StringUtils.hasText(cleaned) ? cleaned : fallback;
    }

    private String buildJewelryFallbackImage(GenerateDesignRequest request) {
        String[] jewelrySamples = new String[]{
                "https://images.unsplash.com/photo-1617038302214-1adff73df0bd?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1617038220319-276d3cfab638?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1611652022419-a9419f74343d?auto=format&fit=crop&w=1200&q=80",
                "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=1200&q=80"
        };
        int index = Math.abs((request.getPrompt() + request.getDesignMode()).hashCode()) % jewelrySamples.length;
        return jewelrySamples[index];
    }

    private String buildImagePrompt(GenerateDesignRequest request) {
        String category = detectJewelryCategory(request);
        StringBuilder builder = new StringBuilder(basePromptPrefix());
        builder.append(categoryTemplate(category, request));
        if (StringUtils.hasText(request.getStyleTags())) {
            builder.append(" 风格标签：").append(request.getStyleTags()).append("。");
        }
        builder.append(" 画面要求：电商主图风格，纯净背景，高清细节，聚焦首饰本体，材质真实，突出金属反光、宝石或珍珠细节。");
        builder.append(" 严禁把项链生成成耳环、戒指、手链、头饰或其他首饰类别。");
        builder.append(" 不要风景，不要人物，不要建筑，不要手部，不要模特，不要多余道具，不要与主题无关的物体。");
        return builder.toString();
    }

    private String basePromptPrefix() {
        return "珠宝首饰设计效果图，一次只生成一张图，主体必须是首饰本体，必须严格匹配用户指定的首饰品类。";
    }

    private String categoryTemplate(String category, GenerateDesignRequest request) {
        String prompt = request.getPrompt();
        return switch (category) {
            case "necklace" -> " 请生成项链/锁骨链设计图。主题：" + prompt
                    + "。主体只能是项链或锁骨链，不能出现耳环、戒指、手链。构图以胸颈佩戴视角或平铺产品视角为主，重点表现链条层次、吊坠结构、珍珠或宝石垂坠感，强调轻奢和佩戴精致度。";
            case "earring" -> " 请生成耳环/耳坠设计图。主题：" + prompt
                    + "。主体只能是耳环或耳坠，不能出现项链、戒指、手链。构图以成对耳饰展示为主，重点表现左右对称、垂坠线条、耳钩或耳夹结构，突出金属工艺和局部高光，适合电商陈列。";
            case "ring" -> " 请生成戒指设计图。主题：" + prompt
                    + "。主体只能是戒指，不能出现项链、耳环、手链。构图以单枚或一组戒指近景特写为主，重点表现戒圈纹理、镶嵌工艺、主石细节和高级质感，突出手工定制感。";
            case "bracelet" -> " 请生成手链/手镯设计图。主题：" + prompt
                    + "。主体只能是手链或手镯，不能出现项链、耳环、戒指。构图以腕饰平铺或半佩戴展示为主，重点表现链节结构、串珠组合、可调节扣位和整体佩戴灵动感。";
            case "headwear" -> " 请生成头饰设计图。主题：" + prompt
                    + "。主体只能是头饰，不能出现项链、耳环、戒指、手链。构图以发箍、发簪或头冠主体展示为主，重点表现装饰层次、花叶结构、缎带或水晶点缀，画面更偏精致写真道具风。";
            case "anklet" -> " 请生成脚链设计图。主题：" + prompt
                    + "。主体只能是脚链，不能出现项链、耳环、戒指、手链。构图以轻盈脚链平铺或脚踝佩戴视角为主，重点表现贝壳、小吊坠、细链和夏日氛围，但主体必须仍然是首饰。";
            default -> " 请生成首饰设计图。主题：" + prompt
                    + "。构图突出首饰主体，强调造型、材质、纹理和适合商品展示的高级感。";
        };
    }

    private String detectJewelryCategory(GenerateDesignRequest request) {
        String text = ((request.getPrompt() == null ? "" : request.getPrompt()) + " "
                + (request.getStyleTags() == null ? "" : request.getStyleTags())).toLowerCase();
        if (containsAny(text, "项链", "锁骨链", "吊坠", "necklace", "pendant")) {
            return "necklace";
        }
        if (containsAny(text, "耳环", "耳坠", "耳饰", "耳钉", "earring")) {
            return "earring";
        }
        if (containsAny(text, "戒指", "对戒", "ring")) {
            return "ring";
        }
        if (containsAny(text, "手链", "手镯", "bracelet", "bangle")) {
            return "bracelet";
        }
        if (containsAny(text, "头饰", "发簪", "发箍", "头冠", "headwear", "hairpin")) {
            return "headwear";
        }
        if (containsAny(text, "脚链", "anklet")) {
            return "anklet";
        }
        return "generic";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private record ImageGenerationResult(String imageUrl, boolean success, String reason) {
    }
}
