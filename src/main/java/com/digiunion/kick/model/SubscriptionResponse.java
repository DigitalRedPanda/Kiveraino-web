package com.digiunion.kick.model;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.List;

public record SubscriptionResponse(
    @JSONField(name = "data")
    List<Data> data,
    @JSONField(name = "message")
    String message
) {
    public static record Data(
        @JSONField(name = "subscription_id")
        String subscriptionId,
        @JSONField(name = "name")
        String name,
        @JSONField(name = "version")
        int version,
        @JSONField(name = "error")
        String error
    ) {}
}
