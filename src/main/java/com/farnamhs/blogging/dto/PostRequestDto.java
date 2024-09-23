package com.farnamhs.blogging.dto;

import java.util.List;

public record PostRequestDto(String title, String content, String category, List<String> tags) {}