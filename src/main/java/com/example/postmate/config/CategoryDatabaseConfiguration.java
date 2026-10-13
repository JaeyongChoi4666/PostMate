package com.example.postmate.config;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/** Initialize the existing database before Hibernate validates the entity schema. */
@Configuration
public class CategoryDatabaseConfiguration {
    @Bean
    DataSourceScriptDatabaseInitializer categoryDatabaseInitializer(DataSource dataSource) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        settings.setMode(DatabaseInitializationMode.ALWAYS);
        settings.setSchemaLocations(List.of("classpath:db/category-visibility.sql"));
        settings.setEncoding(java.nio.charset.StandardCharsets.UTF_8);
        return new DataSourceScriptDatabaseInitializer(dataSource, settings);
    }
}
