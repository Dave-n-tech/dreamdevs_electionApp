package org.electionapp.controller;

import org.electionapp.dto.CreateCandidateRequest;
import org.electionapp.exception.BadRequestException;
import org.electionapp.model.Candidate;
import org.electionapp.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CandidateService candidateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterCandidate() throws Exception {
        CreateCandidateRequest request = new CreateCandidateRequest();
        request.setName("John Doe");
        request.setParty("Party A");
        request.setElectionId("election-1");

        Candidate candidate = new Candidate();
        candidate.setId("1");
        candidate.setName("john doe");
        candidate.setParty("Party A");
        candidate.setElectionId("election-1");

        when(candidateService.registerCandidate(any(CreateCandidateRequest.class)))
                .thenReturn(candidate);

        mockMvc.perform(post("/api/candidates/elections/election-1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("john doe"));

        verify(candidateService).registerCandidate(any(CreateCandidateRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenCandidateExists() throws Exception {
        CreateCandidateRequest request = new CreateCandidateRequest();
        request.setName("John Doe");
        request.setElectionId("election-1");
        request.setParty("AAA");

        when(candidateService.registerCandidate(any()))
                .thenThrow(new BadRequestException("Candidate already registered"));

        mockMvc.perform(post("/api/candidates/elections/election-1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Candidate already registered"));
    }

    @Test
    void shouldGetCandidatesByElectionId() throws Exception {
        Candidate c1 = new Candidate();
        c1.setId("1");
        c1.setName("john");

        Candidate c2 = new Candidate();
        c2.setId("2");
        c2.setName("doe");

        when(candidateService.getCandidatesByElectionId("election-1"))
                .thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/candidates/elections/election-1/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        when(candidateService.getCandidatesByElectionId("election-1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/candidates/elections/election-1/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void shouldGetCandidateById() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setId("1");
        candidate.setName("john doe");

        when(candidateService.getCandidateById("1"))
                .thenReturn(candidate);

        mockMvc.perform(get("/api/candidates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("john doe"));
    }

    @Test
    void shouldReturnNotFoundWhenCandidateDoesNotExist() throws Exception {
        when(candidateService.getCandidateById("1"))
                .thenThrow(new RuntimeException("Candidate not found"));

        mockMvc.perform(get("/api/candidates/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Candidate not found"));
    }

    @Test
    void shouldOverrideElectionIdFromPath() throws Exception {
        CreateCandidateRequest request = new CreateCandidateRequest();
        request.setName("John");
        request.setElectionId("wrong-id");
        request.setParty("AAA");


        Candidate candidate = new Candidate();
        candidate.setId("1");

        when(candidateService.registerCandidate(any()))
                .thenReturn(candidate);

        mockMvc.perform(post("/api/candidates/elections/correct-id/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CreateCandidateRequest> captor =
                ArgumentCaptor.forClass(CreateCandidateRequest.class);

        verify(candidateService).registerCandidate(captor.capture());

        assertEquals("correct-id", captor.getValue().getElectionId());
    }

}