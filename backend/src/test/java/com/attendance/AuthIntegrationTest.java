package com.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void loginWithDefaultAdminShouldReturnTokens() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "admin",
                                "password", "Admin@1234"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", not(emptyString())))
                .andExpect(jsonPath("$.refreshToken", not(emptyString())));
    }

    @Test
    void loginWithWrongPasswordShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "admin",
                                "password", "wrongpassword"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void employeeLoginToAdminConsoleShouldReturn403WithStableErrorCode() throws Exception {
        String adminToken = loginAndGetToken("admin", "Admin@1234");
        String username = "employee_no_admin_" + System.currentTimeMillis();

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", "Test@1234",
                                "fullName", "No Admin Employee",
                                "email", username + "@example.com",
                                "role", "EMPLOYEE"
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", "Test@1234",
                                "clientType", "ADMIN_CONSOLE"
                        ))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is("EMPLOYEE_ACCOUNT_NO_ADMIN_ACCESS")))
                .andExpect(jsonPath("$.message", is("此帳號為員工帳號，無法管理後台")));
    }

    @Test
    void accessAdminEndpointWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessAdminEndpointWithValidAdminTokenShouldReturn200() throws Exception {
        String accessToken = loginAndGetToken("admin", "Admin@1234");

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void inactiveUserTokenShouldNotAccessProtectedEndpoint() throws Exception {
        String adminToken = loginAndGetToken("admin", "Admin@1234");
                String username = "inactive_user_it_" + System.currentTimeMillis();

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", "Test@1234",
                                "fullName", "Inactive User",
                                                                "email", username + "@example.com",
                                "role", "EMPLOYEE"
                        ))))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                                        assert status == 201 : "Expected 201 but got " + status;
                });

        String employeeToken = loginAndGetToken(username, "Test@1234");

        String usersJson = mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .queryParam("size", "50"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long userId = -1L;
        for (var node : objectMapper.readTree(usersJson).path("content")) {
            if (username.equals(node.path("username").asText())) {
                userId = node.path("id").asLong();
                break;
            }
        }
        if (userId < 0) {
            throw new AssertionError("Cannot find test user id for " + username);
        }

        mockMvc.perform(patch("/api/admin/users/{id}/status", userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "INACTIVE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));

        mockMvc.perform(get("/api/attendance/today")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isUnauthorized());
    }

        @Test
        void preflightToProtectedEndpointFromAllowedOriginShouldReturnCorsHeaders() throws Exception {
                mockMvc.perform(options("/api/admin/users")
                                                .header("Origin", "http://localhost:5173")
                                                .header("Access-Control-Request-Method", "GET")
                                                .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        }

        @Test
        void preflightFromDisallowedOriginShouldBeForbidden() throws Exception {
                mockMvc.perform(options("/api/admin/users")
                                                .header("Origin", "http://evil.example")
                                                .header("Access-Control-Request-Method", "GET")
                                                .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                                .andExpect(status().isForbidden())
                                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
        }

        @Test
        void actualRequestFromAllowedOriginShouldKeepCorsHeaders() throws Exception {
                mockMvc.perform(get("/api/admin/users")
                                                .header("Origin", "http://localhost:5173"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        }

    String loginAndGetToken(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
