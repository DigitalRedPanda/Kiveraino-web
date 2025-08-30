package com.digiunion.kick.model;

import org.eclipse.jdt.annotation.Nullable;

import com.alibaba.fastjson2.annotation.JSONField;

public record Badge(
            @JSONField(name = "text")
            String text,

            @JSONField(name = "type")
            String type,

            @JSONField(name = "count")
            @Nullable
            Integer count
            ) {}

