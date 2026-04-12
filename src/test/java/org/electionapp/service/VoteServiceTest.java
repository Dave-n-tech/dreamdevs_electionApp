package org.electionapp.service;

import org.electionapp.dto.VoteRequest;
import org.electionapp.exception.ResourceNotFoundException;
import org.electionapp.model.*;
import org.electionapp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ElectionRepository electionRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private VoterRepository voterRepository;

    @InjectMocks
    private VoteService voteService;

    @Test
    void shouldCastVoteSuccessfully() {
        UUID votingId = UUID.randomUUID();
        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");
        request.setCandidateId("c1");
        request.setVoterId(votingId);

        Election election = new Election();
        election.setId("e1");
        election.setStatus(ElectionStatus.ONGOING);

        Voter voter = new Voter();
        voter.setVotingId(votingId);

        Candidate candidate = new Candidate();
        candidate.setId("c1");
        candidate.setElectionId("e1");

        Vote savedVote = new Vote();
        savedVote.setId("1");

        when(electionRepository.findById("e1"))
                .thenReturn(Optional.of(election));

        when(voterRepository.findByVotingId(votingId))
                .thenReturn(Optional.of(voter));

        when(candidateRepository.findById("c1"))
                .thenReturn(Optional.of(candidate));

        when(voteRepository.findByVoterIdAndElectionId(votingId, "e1"))
                .thenReturn(null);

        when(voteRepository.save(any(Vote.class)))
                .thenReturn(savedVote);

        Vote result = voteService.castVote(request);

        assertNotNull(result);
        assertEquals("1", result.getId());

        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    void shouldThrowWhenElectionNotFound() {

        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");

        when(electionRepository.findById("e1"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> voteService.castVote(request));

        assertEquals("Election not found", ex.getMessage());
    }

    @Test
    void shouldThrowWhenElectionNotOngoing() {

        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");

        Election election = new Election();
        election.setStatus(ElectionStatus.UPCOMING);

        when(electionRepository.findById("e1"))
                .thenReturn(Optional.of(election));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> voteService.castVote(request));

        assertEquals("Election is not ongoing", ex.getMessage());
    }

    @Test
    void shouldThrowWhenVoterNotFound() {

        UUID votingId = UUID.randomUUID();
        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");
        request.setVoterId(votingId);

        Election election = new Election();
        election.setStatus(ElectionStatus.ONGOING);

        when(electionRepository.findById("e1"))
                .thenReturn(Optional.of(election));

        when(voterRepository.findByVotingId(votingId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> voteService.castVote(request));

        assertEquals("Voter not found", ex.getMessage());
    }

    @Test
    void shouldThrowWhenCandidateNotFound() {

        UUID votingId = UUID.randomUUID();
        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");
        request.setVoterId(votingId);
        request.setCandidateId("c1");

        Election election = new Election();
        election.setStatus(ElectionStatus.ONGOING);

        Voter voter = new Voter();
        voter.setVotingId(votingId);

        when(electionRepository.findById("e1"))
                .thenReturn(Optional.of(election));

        when(voterRepository.findByVotingId(votingId))
                .thenReturn(Optional.of(voter));

        when(candidateRepository.findById("c1"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> voteService.castVote(request));

        assertEquals("Candidate not found", ex.getMessage());
    }

    @Test
    void shouldThrowWhenCandidateNotInElection() {

        UUID votingId = UUID.randomUUID();
        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");
        request.setVoterId(votingId);
        request.setCandidateId("c1");

        Election election = new Election();
        election.setStatus(ElectionStatus.ONGOING);

        Voter voter = new Voter();
        voter.setVotingId(votingId);

        Candidate candidate = new Candidate();
        candidate.setId("c1");
        candidate.setElectionId("different-election");

        when(electionRepository.findById("e1"))
                .thenReturn(Optional.of(election));

        when(voterRepository.findByVotingId(votingId))
                .thenReturn(Optional.of(voter));

        when(candidateRepository.findById("c1"))
                .thenReturn(Optional.of(candidate));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> voteService.castVote(request));

        assertEquals("Candidate not in this election", ex.getMessage());
    }

    @Test
    void shouldThrowWhenVoterAlreadyVoted() {

        UUID votingId = UUID.randomUUID();
        VoteRequest request = new VoteRequest();
        request.setElectionId("e1");
        request.setVoterId(votingId);
        request.setCandidateId("c1");

        Election election = new Election();
        election.setStatus(ElectionStatus.ONGOING);

        Voter voter = new Voter();
        voter.setVotingId(votingId);

        Candidate candidate = new Candidate();
        candidate.setId("c1");
        candidate.setElectionId("e1");

        Vote existingVote = new Vote();
        existingVote.setVoterId(votingId);

        when(electionRepository.findById("e1"))
                .thenReturn(Optional.of(election));

        when(voterRepository.findByVotingId(votingId))
                .thenReturn(Optional.of(voter));

        when(candidateRepository.findById("c1"))
                .thenReturn(Optional.of(candidate));

        when(voteRepository.findByVoterIdAndElectionId(votingId, "e1"))
                .thenReturn(existingVote);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> voteService.castVote(request));

        System.out.println(ex.getMessage());
        assertEquals("Voter has already voted", ex.getMessage());
    }

    @Test
    void shouldReturnVotesByElectionId() {

        when(voteRepository.findByElectionId("e1"))
                .thenReturn(List.of(new Vote(), new Vote()));

        List<Vote> result = voteService.getVotesByElectionId("e1");

        assertEquals(2, result.size());
        verify(voteRepository).findByElectionId("e1");
    }

    @Test
    void shouldReturnVotesByCandidateId() {

        when(voteRepository.findByCandidateId("c1"))
                .thenReturn(List.of(new Vote()));

        List<Vote> result = voteService.getVotesByCandidateId("c1");

        assertEquals(1, result.size());
        verify(voteRepository).findByCandidateId("c1");
    }

}