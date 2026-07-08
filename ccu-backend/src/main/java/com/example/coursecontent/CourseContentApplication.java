package com.example.coursecontent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CourseContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseContentApplication.class, args);
    }
}
