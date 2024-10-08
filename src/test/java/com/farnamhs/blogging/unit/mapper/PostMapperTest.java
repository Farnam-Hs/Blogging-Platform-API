package com.farnamhs.blogging.unit.mapper;

import com.farnamhs.blogging.dto.PostRequestDto;
import com.farnamhs.blogging.dto.PostResponseDto;
import com.farnamhs.blogging.entity.Post;
import com.farnamhs.blogging.mapper.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostMapperTest {

    private Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.now();
    }

    @Test
    void should_be_able_to_map_a_request_dto_to_post_entity_with_given_creation_time() {
        PostRequestDto postRequestDto = new PostRequestDto(
                "Title",
                "Content",
                "Category",
                List.of("Java", "Testing")
        );
        Post expectedPost = new Post(
                postRequestDto.title(),
                postRequestDto.content(),
                postRequestDto.category(),
                postRequestDto.tags(),
                now
        );

        Post actualPost = PostMapper.toEntity(postRequestDto, now);

        assertEquals(expectedPost, actualPost);
    }

    @Test
    void should_be_able_to_map_a_request_dto_to_post_entity_with_given_original_post_and_updated_time() {
        PostRequestDto postRequestDto = new PostRequestDto(
                "New Title",
                "New Content",
                "Category",
                List.of("Programming", "Testing")
        );
        Post originalPost = new Post(
                1,
                "Title",
                "Content",
                "Category",
                List.of("Java", "Testing"),
                now.minusSeconds(6000),
                now.minusSeconds(6000)
        );
        Post expectedPost = new Post(
                originalPost.getId(),
                postRequestDto.title(),
                postRequestDto.content(),
                postRequestDto.category(),
                postRequestDto.tags(),
                originalPost.getCreatedAt(),
                now
        );

        Post actualPost = PostMapper.toEntity(postRequestDto, originalPost, now);

        assertEquals(expectedPost, actualPost);
        assertEquals(expectedPost.getId(), actualPost.getId());
    }

    @Test
    void should_be_able_to_map_a_post_to_response_dto() {
        Post post = new Post(
                1,
                "Title",
                "Content",
                "Category",
                List.of("Java", "Testing"),
                now.minusSeconds(6000),
                now
        );
        PostResponseDto expectedResponsePost = new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getTags(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );

        PostResponseDto actualResponsePost = PostMapper.toDto(post);

        assertEquals(expectedResponsePost, actualResponsePost);
    }
}
