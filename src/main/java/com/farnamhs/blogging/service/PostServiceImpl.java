package com.farnamhs.blogging.service;

import com.farnamhs.blogging.dao.PostDao;
import com.farnamhs.blogging.dto.PostRequestDto;
import com.farnamhs.blogging.dto.PostResponseDto;
import com.farnamhs.blogging.entity.Post;
import com.farnamhs.blogging.exception.PostNotFoundException;
import com.farnamhs.blogging.mapper.PostMapper;

import java.util.List;
import java.time.Clock;

import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static com.farnamhs.blogging.mapper.PostMapper.toDto;
import static com.farnamhs.blogging.mapper.PostMapper.toEntity;

public class PostServiceImpl implements PostService {

    private final Clock clock;
    private final PostDao postDao;

    public PostServiceImpl(Clock clock, PostDao postDao) {
        this.clock = clock;
        this.postDao = postDao;
    }

    @Override
    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        validatePostRequest(postRequestDto);

        final Post savedPost = postDao.save(toEntity(postRequestDto, now(clock)));

        return toDto(savedPost);
    }

    @Override
    public PostResponseDto updatePost(long id, PostRequestDto postRequestDto) {
        validatePostRequest(postRequestDto);

        final Post existedPost = fetchPostFromDao(id);
        final Post updatedPost = toEntity(postRequestDto, existedPost, now(clock));
        final boolean isUpdated = postDao.update(updatedPost);

        validateAction(isUpdated);

        return toDto(updatedPost);
    }

    @Override
    public void deletePost(long id) {
        final boolean isDeleted = postDao.deleteById(id);

        validateAction(isDeleted);
    }

    @Override
    public PostResponseDto getPost(long id) {
        return toDto(fetchPostFromDao(id));
    }

    @Override
    public List<PostResponseDto> searchPosts(String searchTerm) {
        return postDao.findBySearchTerm(searchTerm).stream()
                .map(PostMapper::toDto)
                .toList();
    }

    private Post fetchPostFromDao(final long id) {
        return postDao.findById(id).orElseThrow(PostNotFoundException::new);
    }

    private void validatePostRequest(final PostRequestDto postRequestDto) {
        requireNonNull(postRequestDto, "Requested Post Data cannot be null");
    }

    private static void validateAction(final boolean isDone) {
        if (!isDone)
            throw new PostNotFoundException();
    }
}
