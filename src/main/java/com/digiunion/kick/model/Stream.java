package com.digiunion.kick.model;

import com.alibaba.fastjson2.annotation.JSONField;

public record Stream(
        @JSONField(name = "is_live")
        boolean isLive,
        @JSONField(name = "is_mature")
        boolean isMature,
        @JSONField(name = "key")
        String key,
        @JSONField(name = "language")
        String language,
        @JSONField(name = "start_time")
        String startTime,
        @JSONField(name = "thumbnail")
        String thumbnail,
        @JSONField(name = "url")
        String url,
        @JSONField(name = "viewer_count")
        int viewerCount
    ) {}
