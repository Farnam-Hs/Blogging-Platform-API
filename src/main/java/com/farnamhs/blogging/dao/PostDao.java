package com.farnamhs.blogging.dao;

import com.farnamhs.blogging.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {

    Post save(Post post);

    Post update(Post post);

    boolean deleteById(long id);

    Optional<Post> findById(long id);

    List<Post> findBySearchTerm(String searchTerm);
}
