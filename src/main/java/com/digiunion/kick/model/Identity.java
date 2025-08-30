package com.digiunion.kick.model;

import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

public record Identity(
            @JSONField(name = "username_color")
            String usernameColor,

            @JSONField(name = "badges")
            List<Badge> badges
            ) {}

