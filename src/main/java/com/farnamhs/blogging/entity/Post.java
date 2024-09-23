package com.farnamhs.blogging.entity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

public class Post {

    private long id;
    private final String title;
    private final String content;
    private final String category;
    private final List<String> tags;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Post(String title, String content, String category, List<String> tags, Instant createdAt) {
        this(title, content, category, tags, createdAt, createdAt);
    }

    public Post(String title, String content, String category, List<String> tags, Instant createdAt, Instant updatedAt) {
        this.title = validateTitle(title);
        this.content = validateContent(content);
        this.category = validateCategory(category);
        this.tags = validateTags(tags);
        this.createdAt = requireNonNull(createdAt, "Created Time cannot be NULL");
        this.updatedAt = requireNonNull(updatedAt, "Updated Time cannot be NULL");
        validateTimes();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(title, post.title)
                && Objects.equals(content, post.content)
                && Objects.equals(category, post.category)
                && Objects.equals(tags, post.tags)
                && Objects.equals(createdAt, post.createdAt)
                && Objects.equals(updatedAt, post.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, category, tags, createdAt, updatedAt);
    }

    private String validateTitle(String title) {
        return validateString(title, "Title");
    }

    private String validateContent(String content) {
        return validateString(content, "Content");
    }

    private String validateCategory(String category) {
        return validateString(category, "Category");
    }

    private String validateString(String value, String fieldName) {
        String nonNullValue = requireNonNull(value, fieldName + " cannot be NULL");
        if (nonNullValue.isBlank()) throw new IllegalArgumentException(fieldName + " cannot be EMPTY or BLANK");
        return nonNullValue.strip();
    }

    private List<String> validateTags(List<String> tags) {
        return requireNonNull(tags, "Tag list cannot be NULL").stream()
                .filter(Objects::nonNull)
                .filter(not(String::isBlank))
                .map(tag -> tag.strip().toUpperCase())
                .distinct()
                .toList();
    }

    private void validateTimes() {
        if (updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("Updated Time cannot be before the Created Time");
    }
}