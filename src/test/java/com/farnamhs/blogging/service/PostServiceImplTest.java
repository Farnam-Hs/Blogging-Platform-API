package com.farnamhs.blogging.service;

import com.farnamhs.blogging.dao.PostDao;
import com.farnamhs.blogging.dto.PostRequestDto;
import com.farnamhs.blogging.dto.PostResponseDto;
import com.farnamhs.blogging.entity.Post;
import com.farnamhs.blogging.exception.PostNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceImplTest {

    private final Clock fixedClock;

    private PostDao postDao;

    private PostServiceImpl postServiceImpl;

    private PostServiceImplTest() {
        fixedClock = Clock.fixed(Instant.parse("2024-09-21T12:50:00Z"), ZoneId.systemDefault());
    }

    @BeforeEach
    void setUp() {
        postDao = mock(PostDao.class);
        postServiceImpl = new PostServiceImpl(fixedClock, postDao);
    }

    @AfterEach
    void tearDown() {
        reset(postDao);
    }

    @Test
    void should_prevent_if_requested_post_is_null_while_creation_of_a_post() {
        assertThrows(NullPointerException.class, () -> postServiceImpl.createPost(null));
    }

    @Test
    void must_create_and_save_a_post_from_requested_post_and_return_as_a_response_post() {
        PostRequestDto requestedPost = new PostRequestDto(
                "My First Blog Post",
                "This is the content of my first blog post.",
                "Technology",
                List.of("Tech", "Programming")
        );
        Post createdPost = new Post(
                requestedPost.title(),
                requestedPost.content(),
                requestedPost.category(),
                requestedPost.tags(),
                Instant.now(fixedClock)
        );
        Post savedPost = new Post(
                createdPost.getTitle(),
                createdPost.getContent(),
                createdPost.getCategory(),
                createdPost.getTags(),
                createdPost.getCreatedAt()
        );
        savedPost.setId(1);
        PostResponseDto expectedResponsePost = new PostResponseDto(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                savedPost.getCategory(),
                savedPost.getTags(),
                savedPost.getCreatedAt(),
                savedPost.getUpdatedAt()
        );

        when(postDao.save(createdPost)).thenReturn(savedPost);
        PostResponseDto actualResponsePost = postServiceImpl.createPost(requestedPost);

        assertEquals(expectedResponsePost, actualResponsePost);
        verify(postDao).save(createdPost);
    }

    @Test
    void should_prevent_if_requested_post_is_null_while_updating_a_post() {
        assertThrows(NullPointerException.class, () -> postServiceImpl.updatePost(1, null));
    }

    @Test
    void should_throw_exception_if_the_post_does_not_exists_with_that_id_while_updating() {
        PostRequestDto requestedPost = new PostRequestDto(
                "My Updated Blog Post",
                "This is the updated content of my first blog post.",
                "Technology",
                List.of("Tech", "Programming")
        );

        when(postDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postServiceImpl.updatePost(1, requestedPost));
        verify(postDao).findById(1);
    }

    @Test
    void must_update_an_existed_post_from_requested_post_and_return_as_a_response_post() {
        PostRequestDto requestedPost = new PostRequestDto(
                "My Updated Blog Post",
                "This is the updated content of my first blog post.",
                "Technology",
                List.of("Tech", "Programming")
        );
        Post existedPost = new Post(
                "My First Blog Post",
                "This is the content of my first blog post.",
                "Technology",
                List.of("Tech", "Programming"),
                Instant.now(fixedClock)
        );
        existedPost.setId(1);
        Post updatedPost = new Post(
                requestedPost.title(),
                requestedPost.content(),
                requestedPost.category(),
                requestedPost.tags(),
                existedPost.getCreatedAt(),
                Instant.now(fixedClock)
        );
        updatedPost.setId(existedPost.getId());
        PostResponseDto expectedResponsePost = new PostResponseDto(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                updatedPost.getCategory(),
                updatedPost.getTags(),
                updatedPost.getCreatedAt(),
                updatedPost.getUpdatedAt()
        );

        when(postDao.findById(1)).thenReturn(Optional.of(existedPost));
        when(postDao.update(updatedPost)).thenReturn(updatedPost);
        PostResponseDto actualResponsePost = postServiceImpl.updatePost(1, requestedPost);

        assertEquals(expectedResponsePost, actualResponsePost);
        verify(postDao).findById(1);
        verify(postDao).update(updatedPost);
    }

    @Test
    void should_throw_exception_if_post_does_not_exist_while_deleting_a_post_by_its_id() {
        when(postDao.deleteById(1)).thenReturn(false);

        assertThrows(PostNotFoundException.class, () -> postServiceImpl.deletePost(1));
        verify(postDao).deleteById(1);
    }

    @Test
    void should_work_normally_while_deleting_a_post_by_its_id_if_it_exists() {
        when(postDao.deleteById(1)).thenReturn(true);

        assertDoesNotThrow(() -> postServiceImpl.deletePost(1));
        verify(postDao).deleteById(1);
    }

    @Test
    void should_throw_exception_if_post_does_not_exist_while_getting_a_post_by_its_id() {
        when(postDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postServiceImpl.getPost(1));
        verify(postDao).findById(1);
    }

    @Test
    void must_be_able_to_get_a_post_by_its_id_and_return_as_a_response_post() {
        Post existedPost = new Post(
                "My First Blog Post",
                "This is the content of my first blog post.",
                "Technology",
                List.of("Tech", "Programming"),
                Instant.now(fixedClock)
        );
        existedPost.setId(1);
        PostResponseDto expectedResponsePost = new PostResponseDto(
                existedPost.getId(),
                existedPost.getTitle(),
                existedPost.getContent(),
                existedPost.getCategory(),
                existedPost.getTags(),
                existedPost.getCreatedAt(),
                existedPost.getUpdatedAt()
        );

        when(postDao.findById(1)).thenReturn(Optional.of(existedPost));
        PostResponseDto actualResponsePost = postServiceImpl.getPost(1);

        assertEquals(expectedResponsePost, actualResponsePost);
        verify(postDao).findById(1);
    }

    @Test
    void must_be_able_to_get_all_posts_by_a_search_term_and_return_posts_as_a_list_of_response_posts() {
        Post firstPost = new Post(
                "My First Blog Post",
                "This is the content of my first blog post.",
                "Technology",
                List.of("Tech", "Programming"),
                Instant.now(fixedClock)
        );
        firstPost.setId(1);
        Post secondPost = new Post(
                "My Second Blog Post",
                "This is the content of my second blog post.",
                "Technology",
                List.of("Tech", "Programming"),
                Instant.now(fixedClock)
        );
        secondPost.setId(2);
        List<Post> posts = List.of(firstPost, secondPost);
        List<PostResponseDto> expectedResponsePosts = List.of(
                new PostResponseDto(
                        firstPost.getId(),
                        firstPost.getTitle(),
                        firstPost.getContent(),
                        firstPost.getCategory(),
                        firstPost.getTags(),
                        firstPost.getCreatedAt(),
                        firstPost.getUpdatedAt()
                ),
                new PostResponseDto(
                        secondPost.getId(),
                        secondPost.getTitle(),
                        secondPost.getContent(),
                        secondPost.getCategory(),
                        secondPost.getTags(),
                        secondPost.getCreatedAt(),
                        secondPost.getUpdatedAt()
                )
        );

        when(postDao.findBySearchTerm("Tech")).thenReturn(posts);
        List<PostResponseDto> actualResponsePosts = postServiceImpl.searchPosts("Tech");

        assertEquals(expectedResponsePosts, actualResponsePosts);
        verify(postDao).findBySearchTerm("Tech");
    }

    @Test
    void should_return_an_empty_list_when_no_posts_match_the_search_term() {
        when(postDao.findBySearchTerm("None")).thenReturn(List.of());
        List<PostResponseDto> actualResponsePosts = postServiceImpl.searchPosts("None");

        assertTrue(actualResponsePosts.isEmpty());
        verify(postDao).findBySearchTerm("None");
    }
}