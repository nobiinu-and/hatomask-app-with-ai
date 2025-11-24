package com.hatomask.presentation.controller;

import com.hatomask.presentation.dto.PhotoUploadResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final Path uploadDir = Path.of("uploads");

    @AfterEach
    void cleanup() throws Exception {
        if (Files.exists(uploadDir)) {
            Files.list(uploadDir).forEach(p -> p.toFile().delete());
            Files.deleteIfExists(uploadDir);
        }
    }

    @Test
    void アップロードはファイルを保存してレスポンスを返す() throws Exception {
        byte[] content = "dummy".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, content);

        var mvcResult = mockMvc.perform(multipart("/api/v1/photos/upload").file(file))
                .andExpect(status().isOk())
                .andReturn();

        byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
        String body = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        assertThat(body).contains("アップロードに成功しました");

        // parse returned fileName from JSON and assert file exists
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        var node = mapper.readTree(body);
        String storedName = node.get("fileName").asText();
        File stored = uploadDir.resolve(storedName).toFile();
        assertThat(stored.exists()).isTrue();
    }
}
