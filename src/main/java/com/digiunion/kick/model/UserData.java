package com.digiunion.kick.model;

import com.alibaba.fastjson2.annotation.JSONField;

public record UserData(
    @JSONField(name = "data")
    Data[] data,
    @JSONField(name = "message")
    String message
) {
    public record Data(
        @JSONField(name = "email")
        String email,
        @JSONField(name = "name")
        String name,
        @JSONField(name = "profile_picture")
        String profilePicture,
        @JSONField(name = "user_id")
        Long userId 
    ) {}
}
