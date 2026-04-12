package org.electionapp.controller;

import org.electionapp.dto.VoterRequest;
import org.electionapp.model.Voter;
import org.electionapp.service.VoterService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoterController.class)
class VoterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VoterService voterService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- REGISTER ----------------

    @Test
    void shouldRegisterVoterSuccessfully() throws Exception {
        VoterRequest request = new VoterRequest();
        request.setName("John Doe");
        request.setEmail("john@email.com");

        Voter voter = new Voter();
        voter.setName("john doe");
        voter.setEmail("john@email.com");
        voter.setVotingId(UUID.randomUUID());

        Mockito.when(voterService.register(any()))
                .thenReturn(voter);

        mockMvc.perform(post("/api/voters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("john doe"))
                .andExpect(jsonPath("$.email").value("john@email.com"))
                .andExpect(jsonPath("$.votingId").exists());
    }

    @Test
    void shouldReturn400WhenServiceThrowsException() throws Exception {
        VoterRequest request = new VoterRequest();
        request.setName("John Doe");
        request.setEmail("john@email.com");

        Mockito.when(voterService.register(any()))
                .thenThrow(new IllegalArgumentException("Voter already exists"));

        mockMvc.perform(post("/api/voters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Voter already exists"));
    }

    @Test
    void shouldReturn400WhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/voters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ---------------- GET ALL ----------------

    @Test
    void shouldReturnAllVoters() throws Exception {
        Voter v1 = new Voter();
        v1.setVotingId(UUID.randomUUID());

        Voter v2 = new Voter();
        v2.setVotingId(UUID.randomUUID());

        Mockito.when(voterService.getAllVoters())
                .thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/voters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnEmptyListWhenNoVotersExist() throws Exception {
        Mockito.when(voterService.getAllVoters())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/voters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------------- GET BY ID ----------------

    @Test
    void shouldReturnVoterWhenFound() throws Exception {
        UUID id = UUID.randomUUID();

        Voter voter = new Voter();
        voter.setVotingId(id);
        voter.setName("john");
        voter.setEmail("john@doe.com");

        Mockito.when(voterService.getByVotingId(eq(id)))
                .thenReturn(Optional.of(voter));

        mockMvc.perform(get("/api/voters/{votingId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.votingId").value(id.toString()))
                .andExpect(jsonPath("$.name").value("john"))
                .andExpect(jsonPath("$.email").value("john@doe.com"));
    }

    @Test
    void shouldReturn404WhenVoterNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(voterService.getByVotingId(eq(id)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/voters/{votingId}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidUUID() throws Exception {
        mockMvc.perform(get("/api/voters/{votingId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}