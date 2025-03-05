package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.service.AnalysisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * counter控制器
 */
@RestController

public class AnalysisController {

    final AnalysisService analysisService;
    final Logger logger;

    public AnalysisController(@Autowired AnalysisService analysisService) {
        this.analysisService = analysisService;
        this.logger = LoggerFactory.getLogger(AnalysisController.class);
    }

    /**
     * 获取当前解析数据
     *
     * @return API response json
     */
    @GetMapping(value = "/api/analysis/getInfo")
    ApiResponse get(@RequestParam String url) {
        logger.info("/api/analysis/getInfo");
        try {
            if (StringUtils.isEmpty(url)) {
                throw new Exception("请输入分享链接");
            }
            url = url.substring(url.indexOf("https:"));
            Map<String, Object> douYinData = analysisService.getDouYinData(url);
            return ApiResponse.ok(douYinData);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ApiResponse.error(e.getMessage());
        }
    }
}