package com.farnamhs.blogging.controller;

import com.farnamhs.blogging.dto.PostRequestDto;
import com.farnamhs.blogging.dto.PostResponseDto;
import com.farnamhs.blogging.service.PostService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static jakarta.ws.rs.core.Response.*;
import static jakarta.ws.rs.core.Response.Status.*;

@Singleton
@Path("/posts")
public class PostResource {
 
    private final PostService postService;

    @Inject
    public PostResource(PostService postService) {
        this.postService = postService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPost(PostRequestDto postRequestDto) {
        PostResponseDto createdPostResponse = postService.createPost(postRequestDto);
        return status(CREATED).entity(createdPostResponse).build();
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePost(@PathParam("id") long id, PostRequestDto postRequestDto) {
        PostResponseDto updatedPostResponse = postService.updatePost(id, postRequestDto);
        return status(OK).entity(updatedPostResponse).build();
    }

    @DELETE
    @Path("{id}")
    public Response deletePost(@PathParam("id") long id) {
        postService.deletePost(id);
        return noContent().build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPost(@PathParam("id") long id) {
        PostResponseDto postResponse = postService.getPost(id);
        return ok(postResponse).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPosts(@QueryParam("term") @DefaultValue("") String searchTerm) {
        List<PostResponseDto> searchedPostsResponse = postService.searchPosts(searchTerm);
        return ok(searchedPostsResponse).build();
    }
}
