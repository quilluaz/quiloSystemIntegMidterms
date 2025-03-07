package com.quilo.systemintegmidterms.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class DotenvLoader {

    @PostConstruct
    public void loadDotenv() {
        // Load the .env file from the project root
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}
