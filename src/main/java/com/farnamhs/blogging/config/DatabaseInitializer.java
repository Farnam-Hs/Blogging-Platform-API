package com.farnamhs.blogging.config;

import com.farnamhs.blogging.util.PropertiesReader;
import org.flywaydb.core.Flyway;

public class DatabaseInitializer {

    public static void initialize(PropertiesReader reader) throws ClassNotFoundException {
        Class.forName(reader.getProperty("driver"));
        Flyway.configure()
                .dataSource(reader.getProperty("url"), reader.getProperty("user"), reader.getProperty("password"))
                .load()
                .migrate();
    }
}
