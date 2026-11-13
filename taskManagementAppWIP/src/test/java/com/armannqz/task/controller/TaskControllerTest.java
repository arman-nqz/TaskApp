package com.armannqz.task.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String createTask(String title, String priority) throws Exception {
        String response = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "title": "%s",
                                    "description": "Task description",
                                    "priority": "%s"
                                }
                                """, title, priority)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return response.split("\"id\":\"")[1].split("\"")[0];
    }

    @Test
    void testUpdateTaskWithMediumPriority() throws Exception {
        String id = createTask("Test Medium Task", "MEDIUM");

        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Medium Task",
                                    "description": "Updated description",
                                    "priority": "MEDIUM",
                                    "status": "OPEN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.title", is("Updated Medium Task")))
                .andExpect(jsonPath("$.priority", is("MEDIUM")))
                .andExpect(jsonPath("$.status", is("OPEN")));
    }

    @Test
    void testUpdateTaskWithHighPriorityAndDueDate() throws Exception {
        String id = createTask("Test High Task", "HIGH");

        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated High Task",
                                    "description": "Updated description with date",
                                    "dueDate": "2030-12-31",
                                    "priority": "HIGH",
                                    "status": "COMPLETE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.title", is("Updated High Task")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.status", is("COMPLETE")))
                .andExpect(jsonPath("$.dueDate", is("2030-12-31")));
    }

    @Test
    void testUpdateTaskWithLowPriority() throws Exception {
        String id = createTask("Test Low Task", "LOW");

        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Low Task",
                                    "description": "Updated low description",
                                    "priority": "LOW",
                                    "status": "OPEN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.title", is("Updated Low Task")))
                .andExpect(jsonPath("$.priority", is("LOW")));
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/tasks/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Non-existent Task",
                                    "priority": "MEDIUM",
                                    "status": "OPEN"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(String.format("Task with ID '%s' not found", randomId))));
    }

    @Test
    void testUpdateTaskValidationFailure() throws Exception {
        String id = createTask("Test Validation", "HIGH");

        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "",
                                    "priority": "MEDIUM",
                                    "status": "OPEN"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Title must be between 1 and 255 characters")));
    }
}
