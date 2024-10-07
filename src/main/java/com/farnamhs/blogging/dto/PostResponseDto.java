package com.farnamhs.blogging.dto;

import com.farnamhs.blogging.util.InstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;
import java.util.List;

public record PostResponseDto(long id, String title, String content, String category, List<String> tags,
                              @JsonSerialize(using = InstantSerializer.class) Instant createdAt,
                              @JsonSerialize(using = InstantSerializer.class) Instant updatedAt) {}
