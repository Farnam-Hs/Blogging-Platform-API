package com.farnamhs.blogging.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

public class PostTest {

    @Test
    void creation_time_and_update_time_must_be_equal_if_first_constructor_used() {
        Post post = new Post("Title", "Content", "Category", emptyList(), getFixedInstant());

        assertEquals(post.getUpdatedAt(), post.getCreatedAt());
    }

    @Test
    void should_prevent_if_title_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> new Post(null, "Content", "Category", emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_prevent_if_title_is_empty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post("", "Content", "Category", emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_prevent_if_title_is_blank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post("  ", "Content", "Category", emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_trim_the_title() {
        Post post = new Post("  Title  ", "Content", "Category", emptyList(), getFixedInstant(), getFixedInstant());

        assertEquals("Title", post.getTitle());
    }

    @Test
    void should_prevent_if_content_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> new Post("Title", null, "Category", emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_prevent_if_content_is_empty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post("Title", "", "Category", emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_prevent_if_content_is_blank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post("Title", "   ", "Category", emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_trim_the_content() {
        Post post = new Post("Title", "  Content  ", "Category", emptyList(), getFixedInstant(), getFixedInstant());

        assertEquals("Content", post.getContent());
    }

    @Test
    void should_prevent_if_category_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> new Post("Title", "Content", null, emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_prevent_if_category_is_empty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post("Title", "Content", "", emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_prevent_if_category_is_blank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post("Title", "Content", "   ", emptyList(), getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void should_trim_the_category() {
        Post post = new Post("  Title  ", "Content", "  Category  ", emptyList(), getFixedInstant(),
                getFixedInstant());

        assertEquals("Category", post.getCategory());
    }

    @Test
    void should_prevent_if_tags_list_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> new Post("Title", "Content", "Category", null, getFixedInstant(), getFixedInstant())
        );
    }

    @Test
    void must_ignore_the_null_tags_in_the_tag_list() {
        List<String> tags = new LinkedList<>();
        tags.add("FIRST");
        tags.add(null);
        tags.add("THIRD");
        tags.add(null);

        Post post = new Post("Title", "Content", "Category", tags, getFixedInstant(), getFixedInstant());

        assertEquals(List.of("FIRST", "THIRD"), post.getTags());
    }

    @Test
    void must_ignore_the_empty_tags_in_the_tag_list() {
        List<String> tags = new LinkedList<>();
        tags.add("FIRST");
        tags.add("");
        tags.add("THIRD");
        tags.add("");

        Post post = new Post("Title", "Content", "Category", tags, getFixedInstant(), getFixedInstant());

        assertEquals(List.of("FIRST", "THIRD"), post.getTags());
    }

    @Test
    void must_ignore_the_blank_tags_in_the_tag_list() {
        List<String> tags = new LinkedList<>();
        tags.add("FIRST");
        tags.add("  ");
        tags.add("THIRD");
        tags.add("  ");

        Post post = new Post("Title", "Content", "Category", tags, getFixedInstant(), getFixedInstant());

        assertEquals(List.of("FIRST", "THIRD"), post.getTags());
    }

    @Test
    void must_uppercase_and_trim_tags_in_the_tag_list() {
        List<String> tags = new LinkedList<>();
        tags.add(" first");
        tags.add("  thIRd  ");

        Post post = new Post("Title", "Content", "Category", tags, getFixedInstant(), getFixedInstant());

        assertEquals(List.of("FIRST", "THIRD"), post.getTags());
    }

    @Test
    void must_remove_duplicate_tags_in_case_sensitive() {
        List<String> tags = new LinkedList<>();
        tags.add("first");
        tags.add("FiRsT");
        tags.add("FIRST");
        tags.add("thIRd");
        tags.add("THIRD");
        tags.add("third");

        Post post = new Post("Title", "Content", "Category", tags, getFixedInstant(), getFixedInstant());

        assertEquals(List.of("FIRST", "THIRD"), post.getTags());
    }

    @Test
    void must_remove_null_tags_remove_blank_tags_uppercase_and_trim_tags_and_remove_duplicate_tags_in_case_sensitive_all_together() {
        List<String> tags = new LinkedList<>();
        tags.add("first  ");
        tags.add("  FiRsT");
        tags.add(null);
        tags.add("  FIRST  ");
        tags.add("thIRd");
        tags.add("   ");
        tags.add("THIRD");
        tags.add("");
        tags.add("third");

        Post post = new Post("Title", "Content", "Category", tags, getFixedInstant(), getFixedInstant());

        assertEquals(List.of("FIRST", "THIRD"), post.getTags());
    }

    @Test
    void should_prevent_if_creation_time_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> new Post("Title", "Content", "Category", emptyList(), null, getFixedInstant())
        );
    }

    @Test
    void should_prevent_if_update_time_is_null() {
        assertThrows(
                NullPointerException.class,
                () -> new Post("Title", "Content", "Category", emptyList(), getFixedInstant(), null)
        );
    }

    @Test
    void must_prevent_to_set_update_time_before_creation_time() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post("Title", "Content", "Category", emptyList(), getFixedInstant(),
                        getFixedInstant().minusSeconds(10))
        );
    }

    @Test
    void should_be_able_to_set_id() {
        Post post = new Post("Title", "Content", "Category", emptyList(), getFixedInstant(), getFixedInstant());

        post.setId(1);

        assertEquals(1, post.getId());
    }

    @Test
    void two_posts_should_not_be_equal_if_their_titles_are_different() {
        Post firstPost = new Post("First Title", "Content", "Category", emptyList(), getFixedInstant());
        Post secondPost = new Post("Second Title", "Content", "Category", emptyList(), getFixedInstant());

        assertNotEquals(firstPost, secondPost);
    }

    @Test
    void two_posts_should_not_be_equal_if_their_contents_are_different() {
        Post firstPost = new Post("Title", "First Content", "Category", emptyList(), getFixedInstant());
        Post secondPost = new Post("Title", "Second Content", "Category", emptyList(), getFixedInstant());

        assertNotEquals(firstPost, secondPost);
    }

    @Test
    void two_posts_should_not_be_equal_if_their_categories_are_different() {
        Post firstPost = new Post("Title", "Content", "First Category", emptyList(), getFixedInstant());
        Post secondPost = new Post("Title", "Content", "Second Category", emptyList(), getFixedInstant());

        assertNotEquals(firstPost, secondPost);
    }

    @Test
    void two_posts_should_not_be_equal_if_their_tags_are_different() {
        Post firstPost = new Post("Title", "Content", "Category", List.of("FUN"), getFixedInstant());
        Post secondPost = new Post("Title", "Content", "Category", List.of("WAR"), getFixedInstant());

        assertNotEquals(firstPost, secondPost);
    }

    @Test
    void two_posts_should_not_be_equal_if_their_creation_times_are_different() {
        Post firstPost = new Post("Title", "Content", "Category", emptyList(),
                getFixedInstant().minusSeconds(10));
        Post secondPost = new Post("Title", "Content", "Category", emptyList(),
                getFixedInstant().plusSeconds(10));

        assertNotEquals(firstPost, secondPost);
    }

    @Test
    void two_posts_should_not_be_equal_if_their_updated_times_are_different() {
        Post firstPost = new Post("Title", "Content", "Category", emptyList(), getFixedInstant(),
                getFixedInstant().plusSeconds(30));
        Post secondPost = new Post("Title", "Content", "Category", emptyList(), getFixedInstant(),
                getFixedInstant().plusSeconds(60));

        assertNotEquals(firstPost, secondPost);
    }

    @Test
    void two_posts_should_be_equal_if_their_stats_are_equal_except_their_ids() {
        Post firstPost = new Post("Title", "Content", "Category", emptyList(), getFixedInstant());
        firstPost.setId(5);
        Post secondPost = new Post("Title", "Content", "Category", emptyList(), getFixedInstant());
        secondPost.setId(12);

        assertEquals(firstPost, secondPost);
    }

    @Test
    void two_posts_should_have_same_hash_code_if_their_stats_are_equal_except_their_ids() {
        Post firstPost = new Post("Title", "Content", "Category", emptyList(), getFixedInstant());
        firstPost.setId(5);
        Post secondPost = new Post("Title", "Content", "Category", emptyList(), getFixedInstant());
        secondPost.setId(12);

        assertEquals(firstPost.hashCode(), secondPost.hashCode());
    }

    @Test
    void should_be_able_to_create_normally_with_the_first_constructor() {
        long id = 1;
        String title = "Title";
        String content = "Content";
        String category = "Category";
        List<String> tags = emptyList();
        Instant createdAt = getFixedInstant();

        Post post = new Post(title, content, category, tags, createdAt);
        post.setId(id);

        assertEquals(post.getId(), id);
        assertEquals(post.getTitle(), title);
        assertEquals(post.getContent(), content);
        assertEquals(post.getCategory(), category);
        assertEquals(post.getTags(), tags);
        assertEquals(post.getCreatedAt(), createdAt);
        assertEquals(post.getUpdatedAt(), createdAt);
    }

    @Test
    void should_be_able_to_create_normally_with_the_second_constructor() {
        long id = 1;
        String title = "Title";
        String content = "Content";
        String category = "Category";
        List<String> tags = emptyList();
        Instant createdAt = getFixedInstant();
        Instant updatedAt = getFixedInstant().plusSeconds(600);

        Post post = new Post(title, content, category, tags, createdAt, updatedAt);
        post.setId(id);

        assertEquals(post.getId(), id);
        assertEquals(post.getTitle(), title);
        assertEquals(post.getContent(), content);
        assertEquals(post.getCategory(), category);
        assertEquals(post.getTags(), tags);
        assertEquals(post.getCreatedAt(), createdAt);
        assertEquals(post.getUpdatedAt(), updatedAt);
    }

    private Instant getFixedInstant() {
        return Instant.parse("2024-09-20T20:13:00Z");
    }

}