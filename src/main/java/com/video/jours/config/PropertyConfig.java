package com.video.jours.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PropertyConfig {

    @Value("${thread.core-count:2}")
    private int threadCoreCount;

    @Value("${thread.max-count:2}")
    private int threadMaxCount;

    @Value("${thread.max-queue:4}")
    private int maxQueueSize;

}
