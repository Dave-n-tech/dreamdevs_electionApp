package org.electionapp.service;

import org.electionapp.dto.CreateElectionRequest;
import org.electionapp.dto.ElectionResultResponse;
import org.electionapp.model.Candidate;
import org.electionapp.model.Election;
import org.electionapp.model.ElectionStatus;
import org.electionapp.model.Vote;
import org.electionapp.repository.CandidateRepository;
import org.electionapp.repository.ElectionRepository;
import org.electionapp.repository.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElectionServiceTest {
    @Mock
    private ElectionRepository electionRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private ElectionService electionService;

    @Test
    @DisplayName("Should return election if found by Id")
    void shouldReturnElectionIfFoundById(){

        Election election = new Election();
        election.setId("1");
        election.setName("Presidential");
        election.setDescription("Presidential Election 2027");
        election.setStatus(ElectionStatus.UPCOMING);

        when(electionRepository.findById("1"))
                .thenReturn(Optional.of(election));

        Election result = electionService.getElectionById("1");

        assertEquals("1", result.getId());
        verify(electionRepository).findById("1");
    }

    @Test
    @DisplayName("Should throw exception if election not found by Id")
    void shouldThrowIfElectionNotFoundById(){
        when(electionRepository.findById("1"))
                .thenReturn(Optional.empty());

       RuntimeException exception = assertThrows(RuntimeException.class, () -> electionService.getElectionById("1"));

       assertEquals("Election not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should save and return election created with valid request")
    void ReturnElectionCreatedWithValidRequest(){
        CreateElectionRequest request = new CreateElectionRequest();
        request.setName("Election 1");
        request.setDescription("Test Election");
        request.setStartDate(LocalDateTime.now().plusMinutes(1));
        request.setEndDate(LocalDateTime.now().plusDays(1));
        request.setStatus(ElectionStatus.UPCOMING);

        Election savedElection = new Election();
        savedElection.setName("Election 1");

        when(electionRepository.save(any(Election.class))).thenReturn(savedElection);

        Election result = electionService.createElection(request);

        assertEquals("Election 1", result.getName());
        verify(electionRepository).save(any(Election.class));
    }

    @Test
    @DisplayName("Should throw exception when election name is missing")
    void throwExceptionWhenNameIsNull() {
        CreateElectionRequest request = new CreateElectionRequest();
        request.setName(null);
        request.setDescription("Test Description");

        assertThrows(IllegalArgumentException.class, () -> electionService.createElection(request));

        verify(electionRepository, times(0)).save(any(Election.class));
    }

    @Test
    @DisplayName("Should throw exception when start date is in the past")
    void throwExceptionWhenStartDateIsInPast() {
        CreateElectionRequest request = new CreateElectionRequest();
        request.setName("Election 1");
        request.setStartDate(LocalDateTime.now().minusDays(1));
        request.setEndDate(LocalDateTime.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> electionService.createElection(request));

        verify(electionRepository, never()).save(any(Election.class));
    }

    @Test
    @DisplayName("Should change upcoming status to ongoing when election is started")
    void startElectionAndChangeToOngoingWhenUpcoming() {
        Election election = new Election();
        election.setId("1");
        election.setStatus(ElectionStatus.UPCOMING);

        when(electionRepository.findById("1"))
                .thenReturn(Optional.of(election));

        when(electionRepository.save(any(Election.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Election result = electionService.startElection("1");

        assertEquals(ElectionStatus.ONGOING, result.getStatus());
    }

    @Test
    void startElection_whenNotUpcoming_throwsException() {
        Election election = new Election();
        election.setId("1");
        election.setStatus(ElectionStatus.ONGOING);

        when(electionRepository.findById("1"))
                .thenReturn(Optional.of(election));

        assertThrows(IllegalArgumentException.class, () -> electionService.startElection("1"));
    }

    @Test
    @DisplayName("Should change election status from ongoing to ended when election is ended")
    void endElection_whenOngoing_changesToEnded() {
        Election election = new Election();
        election.setId("1");
        election.setStatus(ElectionStatus.ONGOING);

        when(electionRepository.findById("1"))
                .thenReturn(Optional.of(election));

        when(electionRepository.save(any(Election.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Election result = electionService.endElection("1");

        assertEquals(ElectionStatus.ENDED, result.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when trying to end election that is not ongoing")
    void endElectionWhenNotOngoingThrowsException() {
        Election election = new Election();
        election.setStatus(ElectionStatus.UPCOMING);

        when(electionRepository.findById("1"))
                .thenReturn(Optional.of(election));

        assertThrows(IllegalArgumentException.class, () -> electionService.endElection("1"));
    }

    @Test
    @DisplayName("Should throw exception when getting results on election not ended")
    void getResultsWhenElectionNotEndedThrowsException() {
        Election election = new Election();
        election.setStatus(ElectionStatus.ONGOING);

        when(electionRepository.findById("1"))
                .thenReturn(Optional.of(election));

        assertThrows(RuntimeException.class, () -> electionService.getResults("1"));
    }

    @Test
    @DisplayName("Should return zero counts when no votes present")
    void getResultsNoVotesReturnsZeroCounts() {
        Election election = new Election();
        election.setStatus(ElectionStatus.ENDED);

        Candidate candidate = new Candidate();
        candidate.setId("c1");
        candidate.setName("Alice");

        when(electionRepository.findById("1"))
                .thenReturn(Optional.of(election));

        when(voteRepository.findByElectionId("1"))
                .thenReturn(List.of());

        when(candidateRepository.findByElectionId("1"))
                .thenReturn(List.of(candidate));

        List<ElectionResultResponse> result = electionService.getResults("1");

        assertEquals(1, result.size());
        assertEquals(0L, result.getFirst().getVoteCount());
    }

    @Test
    @DisplayName("Should return correct vote counts when votes present")
    void getResultsWithVotesReturnsCorrectCounts() {
        Election election = new Election();
        election.setStatus(ElectionStatus.ENDED);

        Candidate c1 = new Candidate();
        c1.setId("c1");
        c1.setName("Alice");

        Candidate c2 = new Candidate();
        c2.setId("c2");
        c2.setName("Bob");

        Vote v1 = new Vote();
        v1.setCandidateId("c1");
        v1.setElectionId("1");

        Vote v2 = new Vote();
        v2.setCandidateId("c1");
        v2.setElectionId("1");

        Vote v3 = new Vote();
        v3.setCandidateId("c2");
        v3.setElectionId("1");

        when(electionRepository.findById("1"))
                .thenReturn(Optional.of(election));

        when(voteRepository.findByElectionId("1"))
                .thenReturn(List.of(v1, v2, v3));

        when(candidateRepository.findByElectionId("1"))
                .thenReturn(List.of(c1, c2));

        List<ElectionResultResponse> result = electionService.getResults("1");

        Map<String, Long> resultMap = result.stream()
                .collect(Collectors.toMap(
                        ElectionResultResponse::getCandidateId,
                        ElectionResultResponse::getVoteCount
                ));

        assertEquals(2L, resultMap.get("c1"));
        assertEquals(1L, resultMap.get("c2"));
    }





}