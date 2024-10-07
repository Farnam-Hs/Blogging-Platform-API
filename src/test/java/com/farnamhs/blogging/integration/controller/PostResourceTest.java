package com.farnamhs.blogging.integration.controller;

import com.farnamhs.blogging.controller.*;
import com.farnamhs.blogging.dto.PostRequestDto;
import com.farnamhs.blogging.dto.PostResponseDto;
import com.farnamhs.blogging.exception.*;
import com.farnamhs.blogging.service.PostService;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static jakarta.ws.rs.client.Entity.*;
import static jakarta.ws.rs.core.MediaType.*;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.glassfish.jersey.client.ClientProperties.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostResourceTest extends JerseyTest {

    private static PostService postService;

    private static Clock fixedClock;

    @Override
    protected Application configure() {
        return new ResourceConfig().register(new PostResource(postService))
                .register(IllegalArgumentExceptionMapper.class)
                .register(NullPointerExceptionMapper.class)
                .register(PostNotFoundExceptionMapper.class)
                .register(DatabaseExceptionMapper.class)
                .register(GlobalExceptionMapper.class);
    }

    @BeforeAll
    static void beforeAll() {
        postService = mock(PostService.class);
        fixedClock = Clock.fixed(Instant.parse("2024-10-03T12:03:00Z"), ZoneId.systemDefault());
    }

    @AfterEach
    void afterEach() {
        reset(postService);
    }

    @Test
    void should_be_able_to_create_and_return_the_new_post_with_created_status_code_in_json() {
        PostRequestDto request = new PostRequestDto(
                "My First Blog Post",
                "This is the content of my first blog post.",
                "Technology",
                List.of("PROGRAMMING", "TECH")
        );
        PostResponseDto expectedEntityResponse = new PostResponseDto(
                1,
                request.title(),
                request.content(),
                request.category(),
                request.tags(),
                Instant.now(fixedClock),
                Instant.now(fixedClock)
        );

        when(postService.createPost(request)).thenReturn(expectedEntityResponse);
        Response actualResponse = target("posts").request(APPLICATION_JSON).post(entity(request, APPLICATION_JSON));

        assertEquals(CREATED, actualResponse.getStatusInfo());
        assertEquals(APPLICATION_JSON_TYPE, actualResponse.getMediaType());
        assertEquals(expectedEntityResponse, actualResponse.readEntity(PostResponseDto.class));
        verify(postService).createPost(request);
    }

    @Test
    void must_prevent_with_bad_request_status_code_if_there_is_a_validation_error_with_error_message_in_creation() {
        PostRequestDto request = new PostRequestDto(
                " ",
                "This is the content of my first blog post.",
                "Technology",
                List.of("PROGRAMMING", "TECH")
        );

        when(postService.createPost(request)).thenThrow(new IllegalArgumentException("Title cannot be EMPTY or BLANK"));
        Response actualResponse = target("posts").request(APPLICATION_JSON).post(entity(request, APPLICATION_JSON));

        assertEquals(BAD_REQUEST, actualResponse.getStatusInfo());
        assertEquals("Title cannot be EMPTY or BLANK", actualResponse.readEntity(String.class));
        verify(postService).createPost(request);
    }

    @Test
    void must_prevent_with_bad_request_status_code_if_there_is_a_null_value_with_error_message_in_creation() {
        PostRequestDto request = new PostRequestDto(
                "My First Blog Post",
                null,
                "Technology",
                List.of("PROGRAMMING", "TECH")
        );

        when(postService.createPost(request)).thenThrow(new NullPointerException("Content cannot be NULL"));
        Response actualResponse = target("posts").request(APPLICATION_JSON).post(entity(request, APPLICATION_JSON));

        assertEquals(BAD_REQUEST, actualResponse.getStatusInfo());
        assertEquals("Content cannot be NULL", actualResponse.readEntity(String.class));
        verify(postService).createPost(request);
    }

    @Test
    void must_prevent_with_bad_request_status_code_if_request_is_null_in_creation() {
        when(postService.createPost(null)).thenThrow(new NullPointerException("Requested Post Data cannot be null"));
        Response actualResponse = target("posts").request(APPLICATION_JSON).post(entity(null, APPLICATION_JSON));

        assertEquals(BAD_REQUEST, actualResponse.getStatusInfo());
        assertEquals("Requested Post Data cannot be null", actualResponse.readEntity(String.class));
        verify(postService).createPost(null);
    }

    @Test
    void should_be_able_to_update_and_return_the_updated_post_with_ok_status_code_in_json() {
        PostRequestDto request = new PostRequestDto(
                "My Updated Blog Post",
                "This is the updated content of my first blog post.",
                "Technology",
                List.of("PROGRAMMING", "TECH")
        );
        PostResponseDto expectedEntityResponse = new PostResponseDto(
                1,
                request.title(),
                request.content(),
                request.category(),
                request.tags(),
                Instant.now(fixedClock),
                Instant.now(fixedClock).plusSeconds(1800)
        );

        when(postService.updatePost(1, request)).thenReturn(expectedEntityResponse);
        Response actualResponse = target("posts/1").request(APPLICATION_JSON).put(entity(request, APPLICATION_JSON));

        assertEquals(OK, actualResponse.getStatusInfo());
        assertEquals(APPLICATION_JSON_TYPE, actualResponse.getMediaType());
        assertEquals(expectedEntityResponse, actualResponse.readEntity(PostResponseDto.class));
        verify(postService).updatePost(1, request);
    }

    @Test
    void must_prevent_with_bad_request_status_code_if_there_is_a_validation_error_with_error_message_in_updating() {
        PostRequestDto request = new PostRequestDto(
                "My Updated Blog Post",
                "This is the content of my first blog post.",
                "",
                List.of("PROGRAMMING", "TECH")
        );

        when(postService.updatePost(1, request)).thenThrow(new IllegalArgumentException("Category cannot be EMPTY or BLANK"));
        Response actualResponse = target("posts/1").request(APPLICATION_JSON).put(entity(request, APPLICATION_JSON));

        assertEquals(BAD_REQUEST, actualResponse.getStatusInfo());
        assertEquals("Category cannot be EMPTY or BLANK", actualResponse.readEntity(String.class));
        verify(postService).updatePost(1, request);
    }

    @Test
    void must_prevent_with_bad_request_status_code_if_any_field_is_null_with_error_message_in_updating() {
        PostRequestDto request = new PostRequestDto(
                "My Updated Blog Post",
                "This is the content of my first blog post.",
                "",
                null
        );

        when(postService.updatePost(1, request)).thenThrow(new NullPointerException("Tags cannot be NULL"));
        Response actualResponse = target("posts/1").request(APPLICATION_JSON).put(entity(request, APPLICATION_JSON));

        assertEquals(BAD_REQUEST, actualResponse.getStatusInfo());
        assertEquals("Tags cannot be NULL", actualResponse.readEntity(String.class));
        verify(postService).updatePost(1, request);
    }

    @Test
    void must_prevent_with_bad_request_status_code_if_request_is_null_in_updating() {
        when(postService.updatePost(1, null)).thenThrow(new NullPointerException("Requested Post Data cannot be null"));
        Response actualResponse = target("posts/1").property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
                .request(APPLICATION_JSON)
                .put(entity(null, APPLICATION_JSON));

        assertEquals(BAD_REQUEST, actualResponse.getStatusInfo());
        assertEquals("Requested Post Data cannot be null", actualResponse.readEntity(String.class));
        verify(postService).updatePost(1, null);
    }

    @Test
    void must_prevent_if_the_post_does_not_exists_to_update_and_with_not_found_status_code() {
        PostRequestDto request = new PostRequestDto(
                "My Updated Blog Post",
                "This is the updated content of my first blog post.",
                "Technology",
                List.of("PROGRAMMING", "TECH")
        );

        when(postService.updatePost(9999, request)).thenThrow(new PostNotFoundException());
        Response actualResponse = target("posts/9999").request(APPLICATION_JSON).put(entity(request, APPLICATION_JSON));

        assertEquals(NOT_FOUND, actualResponse.getStatusInfo());
        verify(postService).updatePost(9999, request);
    }

    @Test
    void should_be_able_to_delete_a_post_with_no_content_status_code() {
        Response actualResponse = target("posts/1").request().delete();

        assertEquals(NO_CONTENT, actualResponse.getStatusInfo());
        verify(postService).deletePost(1);
    }

    @Test
    void must_prevent_with_not_found_status_code_if_the_post_does_not_exist_to_delete() {
        doThrow(new PostNotFoundException()).when(postService).deletePost(9999);
        Response actualResponse = target("posts/9999").request().delete();

        assertEquals(NOT_FOUND, actualResponse.getStatusInfo());
        verify(postService).deletePost(9999);
    }

    @Test
    void should_be_able_to_get_a_post_with_ok_status_code() {
        PostResponseDto expectedEntityResponse = new PostResponseDto(
                1,
                "My First Blog Post",
                "This is the content of my first blog post.",
                "Technology",
                List.of("PROGRAMMING", "TECH"),
                Instant.now(fixedClock),
                Instant.now(fixedClock)
        );

        when(postService.getPost(1)).thenReturn(expectedEntityResponse);
        Response actualResponse = target("posts/1").request().get();

        assertEquals(OK, actualResponse.getStatusInfo());
        assertEquals(APPLICATION_JSON_TYPE, actualResponse.getMediaType());
        assertEquals(expectedEntityResponse, actualResponse.readEntity(PostResponseDto.class));
        verify(postService).getPost(1);
    }

    @Test
    void must_prevent_with_not_found_status_code_if_the_post_does_not_exist_to_get() {
        when(postService.getPost(9999)).thenThrow(new PostNotFoundException());
        Response actualResponse = target("posts/9999").request().get();

        assertEquals(NOT_FOUND, actualResponse.getStatusInfo());
        verify(postService).getPost(9999);
    }

    @Test
    void should_be_able_to_return_all_posts_with_ok_status_code() {
        List<PostResponseDto> expectedEntitiesResponse = List.of(
                new PostResponseDto(
                        1,
                        "My First Blog Post",
                        "This is the content of my first blog post.",
                        "Technology",
                        List.of("PROGRAMMING", "TECH"),
                        Instant.now(fixedClock),
                        Instant.now(fixedClock)
                ),
                new PostResponseDto(
                        2,
                        "My Second Blog Post",
                        "This is the content of my second blog post.",
                        "Technology",
                        List.of("PROGRAMMING", "TECH"),
                        Instant.now(fixedClock).plusSeconds(1800),
                        Instant.now(fixedClock).plusSeconds(1800)
                )
        );

        when(postService.searchPosts("")).thenReturn(expectedEntitiesResponse);
        Response actualResponse = target("posts").request().get();

        assertEquals(OK, actualResponse.getStatusInfo());
        assertEquals(APPLICATION_JSON_TYPE, actualResponse.getMediaType());
        assertIterableEquals(expectedEntitiesResponse, actualResponse.readEntity(new GenericType<List<PostResponseDto>>() {}));
        verify(postService).searchPosts("");
    }

    @Test
    void should_be_able_to_return_posts_match_to_search_term_parameter_with_ok_status_code() {
        List<PostResponseDto> expectedEntitiesResponse = List.of(
                new PostResponseDto(
                        1,
                        "My First Blog Post",
                        "This is the content of my first blog post.",
                        "Technology",
                        List.of("PROGRAMMING", "TECH"),
                        Instant.now(fixedClock),
                        Instant.now(fixedClock)
                ),
                new PostResponseDto(
                        2,
                        "My Second Blog Post",
                        "This is the content of my second blog post.",
                        "Technology",
                        List.of("PROGRAMMING", "TECH"),
                        Instant.now(fixedClock).plusSeconds(1800),
                        Instant.now(fixedClock).plusSeconds(1800)
                )
        );

        when(postService.searchPosts("tech")).thenReturn(expectedEntitiesResponse);
        Response actualResponse = target("posts").queryParam("term", "tech").request().get();

        assertEquals(OK, actualResponse.getStatusInfo());
        assertEquals(APPLICATION_JSON_TYPE, actualResponse.getMediaType());
        assertIterableEquals(expectedEntitiesResponse, actualResponse.readEntity(new GenericType<List<PostResponseDto>>() {}));
        verify(postService).searchPosts("tech");
    }

    @Test
    void must_handle_database_exception_with_internal_server_error_status_code() {
        when(postService.getPost(1)).thenThrow(new DatabaseException("Some database error!"));
        Response actualResponse = target("posts/1").request().get();

        assertEquals(INTERNAL_SERVER_ERROR, actualResponse.getStatusInfo());
        assertEquals(TEXT_PLAIN_TYPE, actualResponse.getMediaType());
        verify(postService).getPost(1);
    }

    @Test
    void must_handle_other_exceptions_with_internal_server_error_status_code() {
        when(postService.getPost(1)).thenThrow(new RuntimeException("Some unknown error!"));
        Response actualResponse = target("posts/1").request().get();

        assertEquals(INTERNAL_SERVER_ERROR, actualResponse.getStatusInfo());
        assertEquals(TEXT_PLAIN_TYPE, actualResponse.getMediaType());
        verify(postService).getPost(1);
    }
}