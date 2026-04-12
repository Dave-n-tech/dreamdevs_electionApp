package org.electionapp.controller;

import org.mockito.ArgumentCaptor;
import tools.jackson.databind.ObjectMapper;
import org.electionapp.dto.VoteRequest;
import org.electionapp.model.Vote;
import org.electionapp.service.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCastVoteSuccessfully() throws Exception {

        UUID votingId = UUID.randomUUID();
        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");
        request.setCandidateId("c1");
        request.setVoterId(votingId);

        Vote vote = new Vote();
        vote.setId("vote-1");

        when(voteService.castVote(any(VoteRequest.class)))
                .thenReturn(vote);

        mockMvc.perform(post("/api/votes/elections/e1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("vote-1"));

        verify(voteService).castVote(any(VoteRequest.class));
    }

    @Test
    void shouldReturn400WhenRequestBodyIsMissing() throws Exception {

        mockMvc.perform(post("/api/votes/elections/e1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenJsonIsInvalid() throws Exception {

        String invalidJson = "{ invalid }";

        mockMvc.perform(post("/api/votes/elections/e1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldStillCallServiceEvenIfPathElectionIdDiffers() throws Exception {

        UUID votingId = UUID.randomUUID();
        VoteRequest request = new VoteRequest();
        request.setElectionId("wrong-id");
        request.setCandidateId("c1");
        request.setVoterId(votingId);

        when(voteService.castVote(any()))
                .thenReturn(new Vote());

        mockMvc.perform(post("/api/votes/elections/e1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(voteService).castVote(any(VoteRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenElectionNotFound() throws Exception {

        UUID votingId = UUID.randomUUID();
        when(voteService.castVote(any()))
                .thenThrow(new RuntimeException("Election not found"));

        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");
        request.setCandidateId("c1");
        request.setVoterId(votingId);

        mockMvc.perform(post("/api/votes/elections/e1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenAlreadyVoted() throws Exception {

        UUID votingId = UUID.randomUUID();
        when(voteService.castVote(any()))
                .thenThrow(new RuntimeException("Voter has already voted"));

        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");
        request.setCandidateId("c1");
        request.setVoterId(votingId);

        mockMvc.perform(post("/api/votes/elections/e1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Voter has already voted"));
    }

    @Test
    void shouldReturnVotesByElectionId() throws Exception {

        Vote v1 = new Vote();
        v1.setId("1");

        Vote v2 = new Vote();
        v2.setId("2");

        when(voteService.getVotesByElectionId("e1"))
                .thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/votes/elections/e1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnEmptyListWhenNoVotesExist() throws Exception {

        when(voteService.getVotesByElectionId("e1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/votes/elections/e1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnVotesByCandidateId() throws Exception {

        Vote v1 = new Vote();
        v1.setId("1");

        when(voteService.getVotesByCandidateId("c1"))
                .thenReturn(List.of(v1));

        mockMvc.perform(get("/api/votes/candidates/c1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnEmptyListWhenNoVotesForCandidate() throws Exception {

        when(voteService.getVotesByCandidateId("c1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/votes/candidates/c1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldOverrideElectionIdFromPath() throws Exception {

        UUID votingId = UUID.randomUUID();
        VoteRequest request = new VoteRequest();
        request.setElectionId("wrong");
        request.setCandidateId("c1");
        request.setVoterId(votingId);

        when(voteService.castVote(any()))
                .thenReturn(new Vote());

        mockMvc.perform(post("/api/votes/elections/e1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ArgumentCaptor<VoteRequest> captor = ArgumentCaptor.forClass(VoteRequest.class);

        verify(voteService).castVote(captor.capture());

        assert(captor.getValue().getElectionId().equals("e1"));
    }

}