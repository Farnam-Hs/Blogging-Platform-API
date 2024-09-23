package com.farnamhs.blogging.service;

import com.farnamhs.blogging.dto.PostRequestDto;
import com.farnamhs.blogging.dto.PostResponseDto;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(PostRequestDto postRequestDto);

    PostResponseDto updatePost(long id, PostRequestDto postRequestDto);

    void deletePost(long id);

    PostResponseDto getPost(long id);

    List<PostResponseDto> searchPosts(String searchTerm);
}
