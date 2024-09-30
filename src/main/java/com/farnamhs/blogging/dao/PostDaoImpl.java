package com.farnamhs.blogging.dao;

import com.farnamhs.blogging.entity.Post;
import com.farnamhs.blogging.exception.DatabaseException;

import java.sql.*;
import java.time.Instant;
import java.util.*;

import static java.sql.PreparedStatement.*;

public class PostDaoImpl implements PostDao {

    private static final String INSERT_POST_SQL = "INSERT INTO posts" +
            " (title, content, category, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_POST_TAG_SQL = "INSERT INTO post_tags" +
            " (post_id, tag_name) VALUES (?, ?)";
    private static final String UPDATE_POST_SQL = "UPDATE posts" +
            " SET title = ?, content = ?, category = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE_POST_SQL = "DELETE FROM posts" +
            " WHERE id = ?";
    private static final String DELETE_POST_TAGS_SQL = "DELETE FROM post_tags" +
            " WHERE post_id = ?";
    private static final String SELECT_POST_SQL = "SELECT * FROM posts" +
            " WHERE id = ?";
    private static final String SELECT_POSTS_LIKE_SQL = "SELECT * FROM posts" +
            " WHERE title LIKE ? OR content LIKE ? OR category LIKE ?";
    private static final String SELECT_POST_TAGS_NAMES = "SELECT tag_name FROM post_tags" +
            " WHERE post_id = ?";

    private final String url;

    public PostDaoImpl(String url) {
        this.url = url;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public Post save(Post post) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            long postId = insertPost(connection, post);
            insertPostTags(connection, postId, post.getTags());
            Post savedPost = fetchPost(connection, postId).orElseThrow();

            connection.commit();

            return savedPost;
        } catch (NoSuchElementException e) {
            throw new DatabaseException("Unable to find the saved post", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save the post", e);
        }
    }

    @Override
    public Optional<Post> update(Post post) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            boolean isAffected = updatePost(connection, post);
            if (!isAffected) return Optional.empty();
            deletePostTags(connection, post.getId());
            insertPostTags(connection, post.getId(), post.getTags());
            Optional<Post> updatedPost = fetchPost(connection, post.getId());
            if (updatedPost.isEmpty()) throw new DatabaseException("Unable to find the updated post");

            connection.commit();

            return updatedPost;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update the post", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        try (Connection connection = getConnection()) {
            return deletePost(connection, id);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete the post", e);
        }
    }

    @Override
    public Optional<Post> findById(long id) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            Optional<Post> post = fetchPost(connection, id);

            connection.commit();

            return post;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find the post", e);
        }
    }

    @Override
    public List<Post> findBySearchTerm(String searchTerm) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            List<Post> posts = fetchPosts(connection, searchTerm);

            connection.commit();

            return posts;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find the posts", e);
        }
    }

    private long insertPost(Connection connection, Post post) throws SQLException {
        try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_POST_SQL, RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, post.getTitle());
            insertStatement.setString(2, post.getContent());
            insertStatement.setString(3, post.getCategory());
            insertStatement.setObject(4, post.getCreatedAt());
            insertStatement.setObject(5, post.getUpdatedAt());
            insertStatement.execute();
            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        }
    }

    private void insertPostTags(Connection connection, long postId, List<String> tags) throws SQLException {
        try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_POST_TAG_SQL)) {
            for (String tag : tags) {
                insertStatement.setLong(1, postId);
                insertStatement.setString(2, tag);
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
        }
    }

    private boolean updatePost(Connection connection, Post updatedPost) throws SQLException {
        try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_POST_SQL)) {
            updateStatement.setString(1, updatedPost.getTitle());
            updateStatement.setString(2, updatedPost.getContent());
            updateStatement.setString(3, updatedPost.getCategory());
            updateStatement.setObject(4, updatedPost.getUpdatedAt());
            updateStatement.setLong(5, updatedPost.getId());
            return updateStatement.executeUpdate() == 1;
        }
    }

    private void deletePostTags(Connection connection, long postId) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement(DELETE_POST_TAGS_SQL)) {
            deleteStatement.setLong(1, postId);
            deleteStatement.execute();
        }
    }

    private boolean deletePost(Connection connection, long postId) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement(DELETE_POST_SQL)) {
            deleteStatement.setLong(1, postId);
            return deleteStatement.executeUpdate() == 1;
        }
    }

    private List<String> selectPostTags(Connection connection, long postId) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement(SELECT_POST_TAGS_NAMES)) {
            selectStatement.setLong(1, postId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                List<String> tags = new LinkedList<>();
                while (resultSet.next())
                    tags.add(resultSet.getString(1));
                return tags;
            }
        }
    }

    private Optional<Post> fetchPost(Connection connection, long postId) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement(SELECT_POST_SQL)) {
            selectStatement.setLong(1, postId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractPost(resultSet, selectPostTags(connection, postId)));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    private List<Post> fetchPosts(Connection connection, String term) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement(SELECT_POSTS_LIKE_SQL)) {
            String likeTerm = "%" + term + "%";
            selectStatement.setString(1, likeTerm);
            selectStatement.setString(2, likeTerm);
            selectStatement.setString(3, likeTerm);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                List<Post> posts = new LinkedList<>();
                while (resultSet.next())
                    posts.add(extractPost(resultSet, selectPostTags(connection, resultSet.getLong("id"))));
                return posts;
            }
        }
    }

    private Post extractPost(ResultSet postResultSet, List<String> tags) throws SQLException {
        long id = postResultSet.getLong("id");
        String title = postResultSet.getString("title");
        String content = postResultSet.getString("content");
        String category = postResultSet.getString("category");
        Instant createdAt = postResultSet.getTimestamp("created_at").toInstant();
        Instant updatedAt = postResultSet.getTimestamp("updated_at").toInstant();
        return new Post(id, title, content, category, tags, createdAt, updatedAt);
    }
}