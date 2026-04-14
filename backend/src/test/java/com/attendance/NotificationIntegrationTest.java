package com.attendance;

import com.attendance.domain.NotificationRecipient;
import com.attendance.repository.NotificationRecipientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JavaMailSender mailSender;

    @Autowired
    NotificationRecipientRepository recipientRepository;

    @AfterEach
    void cleanUp() {
        recipientRepository.deleteAll();
    }

    @Test
    void testNotificationEndpointSendsEmail() throws Exception {
        MimeMessage mockMime = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMime);

        String adminToken = loginAndGetToken("admin", "Admin@1234");

        mockMvc.perform(post("/api/admin/notification/test")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "hr@example.com"))))
                .andExpect(status().isOk());

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void addRecipientAndListRecipients() throws Exception {
        String adminToken = loginAndGetToken("admin", "Admin@1234");

        mockMvc.perform(post("/api/admin/notification/recipients")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "hr@company.com"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/admin/notification/recipients")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email=='hr@company.com')]").exists());
    }

    @Test
    void duplicateRecipientShouldReturn409() throws Exception {
        String adminToken = loginAndGetToken("admin", "Admin@1234");

        NotificationRecipient existing = new NotificationRecipient();
        existing.setEmail("dup@company.com");
        recipientRepository.save(existing);

        mockMvc.perform(post("/api/admin/notification/recipients")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "dup@company.com"))))
                .andExpect(status().isConflict());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", password
                        ))))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
