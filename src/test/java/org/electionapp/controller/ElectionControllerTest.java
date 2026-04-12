package org.electionapp.controller;

import org.electionapp.dto.CreateElectionRequest;
import org.electionapp.dto.ElectionResultResponse;
import org.electionapp.model.Election;
import org.electionapp.model.ElectionStatus;
import org.electionapp.service.ElectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElectionController.class)
class ElectionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ElectionService electionService;

    @Autowired
    private ObjectMapper objectMapper;

    //
    @Test
    void shouldReturnAllElections() throws Exception {
        List<Election> elections = List.of(new Election(), new Election());

        when(electionService.getAllElections(any()))
                .thenReturn(elections);

        mockMvc.perform(get("/api/elections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnElectionById() throws Exception {
        Election election = new Election();
        election.setId("123");

        when(electionService.getElectionById("123"))
                .thenReturn(election);

        mockMvc.perform(get("/api/elections/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));
    }

    @Test
    void shouldCreateElection() throws Exception {
        CreateElectionRequest request = new CreateElectionRequest();
        request.setName("Test Election");
        request.setStatus(ElectionStatus.UPCOMING);
        request.setStartDate(LocalDateTime.now().plusMinutes(1));
        request.setEndDate(LocalDateTime.now().plusDays(1));

        Election savedElection = new Election();
        savedElection.setId("1");
        savedElection.setName("Test Election");
        savedElection.setStatus(ElectionStatus.UPCOMING);
        savedElection.setDescription("Test Election");
        savedElection.setStartDate(LocalDateTime.now().plusMinutes(1));
        savedElection.setEndDate(LocalDateTime.now().plusDays(1));

        when(electionService.createElection(any()))
                .thenReturn(savedElection);

        mockMvc.perform(post("/api/elections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void shouldStartElection() throws Exception {
        Election election = new Election();
        election.setId("1");

        when(electionService.startElection("1"))
                .thenReturn(election);

        mockMvc.perform(post("/api/elections/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void shouldReturnResults() throws Exception {
        List<ElectionResultResponse> results = List.of(new ElectionResultResponse());

        when(electionService.getResults("1"))
                .thenReturn(results);

        mockMvc.perform(get("/api/elections/1/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    //
    @Test
    void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/elections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/elections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenStatusIsInvalid() throws Exception {
        mockMvc.perform(get("/api/elections")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDateIsInvalid() throws Exception {
        mockMvc.perform(get("/api/elections")
                        .param("startDate", "not-a-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenElectionNotFound() throws Exception {
        when(electionService.getElectionById("1"))
                .thenThrow(new RuntimeException("Election not found"));

        mockMvc.perform(get("/api/elections/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenElectionCannotStart() throws Exception {
        when(electionService.startElection("1"))
                .thenThrow(new IllegalStateException("Election cannot be started"));

        mockMvc.perform(post("/api/elections/1/start"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyListWhenNoResults() throws Exception {
        when(electionService.getResults("1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/elections/1/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn400WhenValidationFails() throws Exception {
        CreateElectionRequest request = new CreateElectionRequest(); // missing name

        mockMvc.perform(post("/api/elections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}