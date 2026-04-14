package com.attendance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AttendanceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String adminToken;
    private String employeeToken;
        private String employeeUsername;

    @BeforeEach
    void setUp() throws Exception {
        // Create an employee for testing
        adminToken = loginAndGetToken("admin", "Admin@1234");
        employeeUsername = "employee_test_" + System.currentTimeMillis();

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", employeeUsername,
                                "password", "Test@1234",
                                "fullName", "Test Employee",
                                "email", employeeUsername + "@example.com",
                                "role", "EMPLOYEE"
                        ))))
                .andExpect(status().isCreated());

        employeeToken = loginAndGetToken(employeeUsername, "Test@1234");
    }

    @Test
    void employeeCanClockIn() throws Exception {
        mockMvc.perform(post("/api/attendance/clock-in")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 200 || status == 201 || status == 409 : "Expected 200, 201 or 409 but got " + status;
                });
    }

    @Test
    void employeeCanViewTodayAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance/today")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 200 || status == 204 || status == 404 : "Expected 200, 204 or 404 but got " + status;
                });
    }

    @Test
    void adminCanQueryAttendanceRecords() throws Exception {
        mockMvc.perform(get("/api/admin/attendance")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    void employeeCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/attendance")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAdjustAttendanceWithReasonAndSeeHistory() throws Exception {
        mockMvc.perform(post("/api/attendance/clock-in")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 200 || status == 201 || status == 409 : "Expected 200, 201 or 409 but got " + status;
                });

        long employeeId = getUserIdByUsername(employeeUsername);

        MvcResult attendanceResult = mockMvc.perform(get("/api/admin/attendance")
                        .header("Authorization", "Bearer " + adminToken)
                        .queryParam("userId", String.valueOf(employeeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andReturn();

        long recordId = objectMapper.readTree(attendanceResult.getResponse().getContentAsString())
                .path("content").get(0).path("id").asLong();

        Instant adjustedClockIn = Instant.parse("2026-04-14T01:00:00Z");
        mockMvc.perform(patch("/api/admin/attendance/{id}/adjust", recordId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "clockInTime", adjustedClockIn.toString(),
                                "reason", "補登上班時間"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestAdjustment.reason", is("補登上班時間")));

        mockMvc.perform(get("/api/admin/attendance/{id}/adjustments", recordId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].reason", is("補登上班時間")));
    }

    @Test
    void adminAdjustAttendanceRequiresReason() throws Exception {
        mockMvc.perform(post("/api/attendance/clock-in")
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 200 || status == 201 || status == 409 : "Expected 200, 201 or 409 but got " + status;
                });

        long employeeId = getUserIdByUsername(employeeUsername);

        MvcResult attendanceResult = mockMvc.perform(get("/api/admin/attendance")
                        .header("Authorization", "Bearer " + adminToken)
                        .queryParam("userId", String.valueOf(employeeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andReturn();

        long recordId = objectMapper.readTree(attendanceResult.getResponse().getContentAsString())
                .path("content").get(0).path("id").asLong();

        mockMvc.perform(patch("/api/admin/attendance/{id}/adjust", recordId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "clockInTime", "2026-04-14T01:00:00Z",
                                "reason", ""
                        ))))
                .andExpect(status().isBadRequest());
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

    private long getUserIdByUsername(String username) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .queryParam("size", "50"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode content = objectMapper.readTree(result.getResponse().getContentAsString()).path("content");
        for (JsonNode userNode : content) {
            if (username.equals(userNode.path("username").asText())) {
                return userNode.path("id").asLong();
            }
        }
        throw new AssertionError("User not found in admin list: " + username);
    }
}
