package com.farnamhs.blogging.mapper;

import com.farnamhs.blogging.dto.PostRequestDto;
import com.farnamhs.blogging.dto.PostResponseDto;
import com.farnamhs.blogging.entity.Post;

import java.time.Instant;

public class PostMapper {

    public static Post toEntity(PostRequestDto postRequestDto, Instant createdAt) {
        return new Post(
                postRequestDto.title(),
                postRequestDto.content(),
                postRequestDto.category(),
                postRequestDto.tags(),
                createdAt
        );
    }

    public static Post toEntity(PostRequestDto postRequestDto, Post originalPost, Instant updatedAt) {
        Post post = new Post(
                postRequestDto.title(),
                postRequestDto.content(),
                postRequestDto.category(),
                postRequestDto.tags(),
                originalPost.getCreatedAt(),
                updatedAt
        );
        post.setId(originalPost.getId());

        return post;
    }

    public static PostResponseDto toDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getTags(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}