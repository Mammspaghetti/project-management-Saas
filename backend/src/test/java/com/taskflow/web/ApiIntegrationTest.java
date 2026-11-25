package com.taskflow.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** End-to-end API test against a real MySQL (Testcontainers): auth, RBAC, task lifecycle. */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class ApiIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private JsonNode register(String email) throws Exception {
        String body = """
                {"email":"%s","password":"Passw0rd!","displayName":"%s"}""".formatted(email, email);
        String res = mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(res);
    }

    private static String bearer(JsonNode auth) {
        return "Bearer " + auth.get("accessToken").asText();
    }

    private static String unique(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@test.io";
    }

    @Test
    void unauthenticated_requests_are_rejected() throws Exception {
        mvc.perform(get("/api/v1/projects")).andExpect(status().isUnauthorized());
    }

    @Test
    void login_with_wrong_password_returns_401_problem_json() throws Exception {
        String email = unique("login");
        register(email);

        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"wrong-password\"}".formatted(email)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalid_payload_returns_400_with_field_errors() throws Exception {
        mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"x\",\"displayName\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void rbac_owner_viewer_and_outsider() throws Exception {
        JsonNode owner = register(unique("owner"));
        JsonNode viewer = register(unique("viewer"));
        JsonNode outsider = register(unique("outsider"));

        String projectJson = mvc.perform(post("/api/v1/projects").header("Authorization", bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Website\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long projectId = json.readTree(projectJson).get("id").asLong();
        String base = "/api/v1/projects/" + projectId;

        // owner adds the viewer
        mvc.perform(post(base + "/members").header("Authorization", bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"role\":\"VIEWER\"}".formatted(viewer.at("/user/email").asText())))
                .andExpect(status().isCreated());

        // owner creates a task
        mvc.perform(post(base + "/tasks").header("Authorization", bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Design homepage\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"));

        // viewer: can read, cannot write
        mvc.perform(get(base + "/tasks").header("Authorization", bearer(viewer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(post(base + "/tasks").header("Authorization", bearer(viewer))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Nope\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete(base).header("Authorization", bearer(viewer))).andExpect(status().isForbidden());

        // outsider: project is invisible, and absent from their list
        mvc.perform(get(base).header("Authorization", bearer(outsider))).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/projects").header("Authorization", bearer(outsider)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));

        // non-admin cannot reach admin endpoints
        mvc.perform(get("/api/v1/admin/users").header("Authorization", bearer(owner)))
                .andExpect(status().isForbidden());
    }
}
