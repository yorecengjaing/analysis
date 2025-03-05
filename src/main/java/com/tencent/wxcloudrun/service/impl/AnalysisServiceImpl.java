package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.constant.AnalysisConstant;
import com.tencent.wxcloudrun.service.AnalysisService;
import com.tencent.wxcloudrun.util.AnalysisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Override
    public Map<String, Object> getDouYinData(String url) throws Exception {
        Map<String, Object> resultObj = new HashMap<>();
        String content = AnalysisUtil.safeCrawl(url);
        Document document = Jsoup.parse(content);
        Element videoPlayer = document.getElementById("video-player");
        if (videoPlayer == null) {
            throw new Exception("视频对象不存在");
        }
        String src = videoPlayer.attr("src");
        src = src.replace("playwm", "play");
        src = AnalysisConstant.DouYinHome + src;
        resultObj.put("videoUrl", src);
        return resultObj;
    }
}
