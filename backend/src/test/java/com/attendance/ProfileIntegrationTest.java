package com.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProfileIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String employeeToken;
    private String employeeUsername;

    @BeforeEach
    void setUp() throws Exception {
        String adminToken = loginAndGetToken("admin", "Admin@1234");
        employeeUsername = "profile_test_" + System.currentTimeMillis();

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", employeeUsername,
                                "password", "Test@1234",
                                "fullName", "Original Name",
                                "email", employeeUsername + "@example.com",
                                "role", "EMPLOYEE"
                        ))))
                .andExpect(status().isCreated());

        employeeToken = loginAndGetToken(employeeUsername, "Test@1234");
    }

    @Test
    void getMeShouldReturnCurrentUser() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(employeeUsername)))
                .andExpect(jsonPath("$.fullName", is("Original Name")));
    }

    @Test
    void getMeWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfileShouldUpdateFullNameAndEmail() throws Exception {
        String newEmail = "updated_" + System.currentTimeMillis() + "@example.com";

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "fullName", "Updated Name",
                                "email", newEmail
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Updated Name")))
                .andExpect(jsonPath("$.email", is(newEmail)))
                .andExpect(jsonPath("$.username", is(employeeUsername))); // username unchanged
    }

    @Test
    void updateProfileWithEmptyPasswordShouldReturn400() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("password", "");

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfileWithDuplicateEmailShouldReturn409() throws Exception {
        // Register another user with a known email
        String adminToken = loginAndGetToken("admin", "Admin@1234");
        String otherEmail = "other_conflict_" + System.currentTimeMillis() + "@example.com";
        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "other_user_" + System.currentTimeMillis(),
                                "password", "Test@1234",
                                "fullName", "Other User",
                                "email", otherEmail,
                                "role", "EMPLOYEE"
                        ))))
                .andExpect(status().isCreated());

        // Try to update our user's email to conflict
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", otherEmail))))
                .andExpect(status().isConflict());
    }

    @Test
    void updateProfileWithValidPasswordShouldAllowLogin() throws Exception {
        String newPassword = "NewPass@5678";
        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", newPassword))))
                .andExpect(status().isOk());

        // Verify new password works
        String newToken = loginAndGetToken(employeeUsername, newPassword);
        org.assertj.core.api.Assertions.assertThat(newToken).isNotBlank();
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
