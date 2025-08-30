package com.digiunion.kick.model;

import com.alibaba.fastjson2.annotation.JSONField;

public record Channel(
        @JSONField(name = "banner_picture")
        String bannerPicture,
        @JSONField(name = "broadcaster_user_id")
        long broadcasterUserId,
        @JSONField(name = "category")
        Category category,
        @JSONField(name = "channel_description")
        String channelDescription,
        @JSONField(name = "slug")
        String slug,
        @JSONField(name = "stream")
        Stream stream,
        @JSONField(name = "stream_title")
        String streamTitle
    ) {}
