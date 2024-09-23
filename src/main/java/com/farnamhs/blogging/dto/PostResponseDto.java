package com.farnamhs.blogging.dto;

import com.farnamhs.blogging.config.InstantSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;
import java.util.List;

public record PostResponseDto(@JsonProperty("id")
                              long id,
                              @JsonProperty("title")
                              String title,
                              @JsonProperty("content")
                              String content,
                              @JsonProperty("category")
                              String category,
                              @JsonProperty("tags")
                              List<String> tags,
                              @JsonProperty("createdAt") @JsonSerialize(using = InstantSerializer.class)
                              Instant createdAt,
                              @JsonProperty("updatedAt") @JsonSerialize(using = InstantSerializer.class)
                              Instant updatedAt) {
}
