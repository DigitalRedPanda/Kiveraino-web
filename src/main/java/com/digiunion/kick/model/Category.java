package com.digiunion.kick.model;

import com.alibaba.fastjson2.annotation.JSONField;

public record Category(
            @JSONField(name = "id")
            long id,
            @JSONField(name = "name")
            String name,
            @JSONField(name = "thumbnail")
            String thumbnail
                      ) {}
