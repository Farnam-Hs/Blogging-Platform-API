package com.farnamhs.blogging.config;

import com.farnamhs.blogging.controller.*;
import com.farnamhs.blogging.dao.PostDao;
import com.farnamhs.blogging.dao.PostDaoImpl;
import com.farnamhs.blogging.exception.*;
import com.farnamhs.blogging.service.PostService;
import com.farnamhs.blogging.service.PostServiceImpl;
import com.farnamhs.blogging.util.PropertiesReader;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

import java.time.Clock;

@ApplicationPath("/api")
public class Application extends ResourceConfig {
    public Application() {
        try {
            PropertiesReader propertiesReader = new PropertiesReader("database.properties");
            DatabaseInitializer.initialize(propertiesReader);
            Clock utcClock = Clock.systemUTC();
            PostDao postDao = new PostDaoImpl(propertiesReader.getProperty("url"));
            registerResources(new PostServiceImpl(utcClock, postDao));
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    private void registerResources(PostService postService) {
        register(new PostResource(postService));
        register(PostNotFoundExceptionMapper.class);
        register(DatabaseExceptionMapper.class);
        register(IllegalArgumentExceptionMapper.class);
        register(NullPointerExceptionMapper.class);
        register(GlobalExceptionMapper.class);
    }
}