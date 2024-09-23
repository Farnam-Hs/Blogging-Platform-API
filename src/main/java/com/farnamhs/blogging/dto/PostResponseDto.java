package com.farnamhs.blogging.dto;

import java.time.Instant;
import java.util.List;

public record PostResponseDto(long id, String title, String content, String category, List<String> tags, Instant createdAt, Instant updatedAt) {}
