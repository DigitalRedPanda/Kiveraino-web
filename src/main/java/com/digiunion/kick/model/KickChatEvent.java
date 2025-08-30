package com.digiunion.kick.model;


import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.alibaba.fastjson2.annotation.JSONField;



public record KickChatEvent(
        @JSONField(name = "message_id")
        String messageId,

        @JSONField(name = "broadcaster")
        User broadcaster,

        @JSONField(name = "sender")
        User sender,

        @JSONField(name = "content")
        String content,

        @JSONField(name = "emotes")
        List<Emote> emotes,

        EventHeaders eventHeaders
        ) {

    public record Emote(
            @JSONField(name = "emote_id")
            String emoteId,

            @JSONField(name = "positions")
            List<Position> positions
            ) {}
    public record Position(
            @JSONField(name = "s")
            int start,

            @JSONField(name = "e")
            int end
            ) {}  
    public record EventHeaders(
            String eventType,  // Maps to "Kick-Event-Type"
            String version     // Maps to "Kick-Event-Version"
            ) {}
        }
