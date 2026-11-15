package com.diy.jewelry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    private String endpoint;
    private String imageEndpoint;
    private String apiKey;
    private String imageApiKey;
    private String model;
    private String imageModel;
    private String fallbackImageBase;
}
