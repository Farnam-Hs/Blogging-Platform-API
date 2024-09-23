package com.farnamhs.blogging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PostRequestDto(@JsonProperty("title")
                             String title,
                             @JsonProperty("content")
                             String content,
                             @JsonProperty("category")
                             String category,
                             @JsonProperty("tags")
                             List<String> tags) {
}