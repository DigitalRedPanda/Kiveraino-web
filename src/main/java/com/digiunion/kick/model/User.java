package com.digiunion.kick.model;

import org.eclipse.jdt.annotation.Nullable;

import com.alibaba.fastjson2.annotation.JSONField;

public record User(
            @JSONField(name = "is_anonymous")
            boolean isAnonymous,

            @JSONField(name = "user_id")
            long userId,

            @JSONField(name = "username")
            String username,

            @JSONField(name = "is_verified")
            boolean isVerified,

            @JSONField(name = "profile_picture")
            String profilePicture,

            @JSONField(name = "channel_slug")
            String channelSlug,

            @JSONField(name = "identity")
            @Nullable
            Identity identity    
            ) {}

