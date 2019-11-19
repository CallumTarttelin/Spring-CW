package com.example.recycling;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    // Build the app into a WAR if bootWar ran
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RecyclingApplication.class);
    }

}
