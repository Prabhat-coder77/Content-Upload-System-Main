package com.example.coursecontent.controller;

import com.example.coursecontent.BaseIntegrationTest;
import com.example.coursecontent.dto.request.RegisterReq;
import com.example.coursecontent.dto.response.TokenRes;
import com.example.coursecontent.repository.CourseContentRepository;
import com.example.coursecontent.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ContentIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseContentRepository contentRepository;

    private String jwtToken;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        contentRepository.deleteAll();

        // Create directory for local storage test
        Path uploadsDir = Paths.get("/tmp/ccu_uploads");
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }

        // Register user and get token
        RegisterReq registerReq = new RegisterReq();
        registerReq.setUsername("testuser_content");
        registerReq.setEmail("testcontent@test.com");
        registerReq.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract token from inside the `data` field of the StandardResponse
        String jsonResponse = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(jsonResponse).get("data").get("accessToken").asText();
    }

    @AfterEach
    void tearDown() {
        contentRepository.deleteAll();
        userRepository.deleteAll();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder post(String url) {
        return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url);
    }

    @Test
    void testUploadAndListContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "Dummy PDF content".getBytes()
        );

        // 1. Upload
        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/contents")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value("test-document.pdf"))
                .andReturn();

        String responseStr = uploadResult.getResponse().getContentAsString();
        String fileId = objectMapper.readTree(responseStr).get("data").get("id").asText();

        // 2. List
        mockMvc.perform(get("/api/v1/contents")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(fileId));

        // 3. Get single
        mockMvc.perform(get("/api/v1/contents/" + fileId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(fileId));

        // 4. Download
        mockMvc.perform(get("/api/v1/contents/" + fileId + "/download")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}
