package com.farnamhs.blogging.integration.dao;

import com.farnamhs.blogging.dao.PostDaoImpl;
import com.farnamhs.blogging.entity.Post;
import com.farnamhs.blogging.util.PropertiesReader;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.*;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostDaoImplTest {

    private Clock fixedClock;

    private String url;

    private Connection connection;

    private PostDaoImpl postDaoImpl;

    @BeforeAll
    void beforeAll() throws IOException, SQLException, ClassNotFoundException {
        fixedClock = Clock.fixed(Instant.parse("2024-09-29T17:47:25Z"), ZoneId.systemDefault());
        PropertiesReader reader = new PropertiesReader("test-database.properties");
        url = reader.getProperty("url");
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(url);
        Class.forName(reader.getProperty("driver"));
        connection = dataSource.getConnection();
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }

    @BeforeEach
    void setUp() throws IOException, SQLException {
        InputStreamReader dataInputStreamReader = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("data.sql")));
        RunScript.execute(connection, dataInputStreamReader);
        dataInputStreamReader.close();
        postDaoImpl = new PostDaoImpl(url);
    }

    @AfterAll
    void afterAll() throws SQLException {
        connection.close();
    }

    @Test
    void must_be_able_to_save_a_new_post_into_the_database() {
        Post post = new Post(
                "New Post",
                "The content of the new post",
                "Misc",
                List.of("FRESH", "NEW"),
                Instant.now(fixedClock)
        );
        Post expected = new Post(
                4,
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getTags(),
                Instant.now(fixedClock),
                Instant.now(fixedClock)
        );

        Post actual = postDaoImpl.save(post);

        assertEquals(expected, actual);
    }

    @Test
    void should_return_empty_if_post_does_not_exist_to_update() {
        Post expectedPost = new Post(
                9999,
                "My Updated Blog Post",
                "This is the updated content of my first blog post.",
                "Technology",
                List.of("PROGRAMMING", "TECH"),
                Instant.now(fixedClock),
                Instant.now(fixedClock)
        );

        Optional<Post> actual = postDaoImpl.update(expectedPost);

        assertTrue(actual.isEmpty());
    }

    @Test
    void must_be_able_to_update_an_existing_post_in_the_database() {
        Post post = new Post(
                1,
                "My Updated Blog Post",
                "This is the updated content of my first blog post.",
                "Technology",
                List.of("PROGRAMMING", "TECH"),
                Instant.parse("2023-06-14T17:44:00Z"),
                Instant.now(fixedClock)
        );
        Optional<Post> expected = Optional.of(
                new Post(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCategory(),
                        post.getTags(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                )
        );

        Optional<Post> actual = postDaoImpl.update(post);

        assertEquals(expected, actual);
    }

    @Test
    void should_return_false_if_post_does_not_exist_to_delete() {
        boolean actual = postDaoImpl.deleteById(9999);

        assertFalse(actual);
    }

    @Test
    void must_be_able_to_delete_an_existing_post_from_the_database() {
        boolean actual = postDaoImpl.deleteById(1);

        assertTrue(actual);
        assertTrue(postDaoImpl.findById(1).isEmpty());
    }

    @Test
    void should_be_empty_if_finding_a_non_exist_post_from_the_database() {
        Optional<Post> actual = postDaoImpl.findById(9999);

        assertTrue(actual.isEmpty());
    }

    @Test
    void must_be_able_to_find_an_existed_post_in_the_database() {
        Optional<Post> expected = Optional.of(
                new Post(
                        1,
                        "Updated Post 1",
                        "This is the content for post 1",
                        "Category 1",
                        List.of("INTERNET", "TECHNOLOGY"),
                        Instant.parse("2023-06-14T17:44:00Z"),
                        Instant.parse("2024-09-29T03:46:32Z")
                )
        );

        Optional<Post> actual = postDaoImpl.findById(1);

        assertEquals(expected, actual);
    }

    @Test
    void should_return_an_empty_list_if_search_term_does_not_match() {
        List<Post> actual = postDaoImpl.findBySearchTerm("NothingMatchesThisTerm");

        assertTrue(actual.isEmpty());
    }

    @Test
    void must_be_able_to_find_a_list_of_posts_with_a_search_term_in_the_database() {
        List<Post> expected = List.of(
                new Post(
                        2,
                        "New Post 2",
                        "This is the content for post 2",
                        "Category 2",
                        List.of("COMPUTER", "JAVA", "PROGRAMMING"),
                        Instant.parse("2023-11-17T09:11:32Z"),
                        Instant.parse("2023-11-17T09:11:32Z")
                ),
                new Post(
                        3,
                        "New Post 3",
                        "This is the content for post 3",
                        "Category 3",
                        List.of(),
                        Instant.parse("2024-09-29T17:45:30Z"),
                        Instant.parse("2024-09-29T17:45:30Z")
                )
        );

        List<Post> actual = postDaoImpl.findBySearchTerm("New");

        assertIterableEquals(expected, actual);
    }
}
