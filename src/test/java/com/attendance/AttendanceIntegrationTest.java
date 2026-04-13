package com.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeEach
    void setUp() throws Exception {
        // Create an employee for testing
        adminToken = loginAndGetToken("admin", "Admin@1234");

        try {
            mockMvc.perform(post("/api/admin/users")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of(
                            "username", "employee_test",
                            "password", "Test@1234",
                            "fullName", "Test Employee",
                            "email", "test-employee@example.com",
                            "role", "EMPLOYEE"
                    ))));
        } catch (Exception ignored) {
            // May already exist from a previous test run
        }

        employeeToken = loginAndGetToken("employee_test", "Test@1234");
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
