package org.electionapp.service;

import org.electionapp.dto.CreateCandidateRequest;
import org.electionapp.exception.BadRequestException;
import org.electionapp.exception.ResourceNotFoundException;
import org.electionapp.model.Candidate;
import org.electionapp.repository.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateService candidateService;

    @Test
    void shouldRegisterCandidateSuccessfully() {
        CreateCandidateRequest request = new CreateCandidateRequest();
        request.setName("John Doe");
        request.setParty("Party A");
        request.setElectionId("election-1");

        when(candidateRepository.existsByNameAndElectionId("John Doe", "election-1"))
                .thenReturn(false);

        Candidate savedCandidate = new Candidate();
        savedCandidate.setId("1");
        savedCandidate.setName("john doe");
        savedCandidate.setParty("Party A");
        savedCandidate.setElectionId("election-1");

        when(candidateRepository.save(any(Candidate.class)))
                .thenReturn(savedCandidate);

        Candidate result = candidateService.registerCandidate(request);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("john doe", result.getName());
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void shouldTrimAndLowercaseCandidateName() {
        CreateCandidateRequest request = new CreateCandidateRequest();
        request.setName("  JOHN DOE  ");
        request.setParty("Party A");
        request.setElectionId("election-1");

        when(candidateRepository.existsByNameAndElectionId(any(), any()))
                .thenReturn(false);

        when(candidateRepository.save(any(Candidate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Candidate result = candidateService.registerCandidate(request);

        assertEquals("john doe", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenCandidateAlreadyExists() {
        CreateCandidateRequest request = new CreateCandidateRequest();
        request.setName("John Doe");
        request.setElectionId("election-1");

        when(candidateRepository.existsByNameAndElectionId("John Doe", "election-1"))
                .thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                candidateService.registerCandidate(request)
        );

        assertEquals("Candidate already registered", exception.getMessage());

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void shouldReturnCandidatesByElectionId() {
        List<Candidate> candidates = List.of(new Candidate(), new Candidate());

        when(candidateRepository.findByElectionId("election-1"))
                .thenReturn(candidates);

        List<Candidate> result = candidateService.getCandidatesByElectionId("election-1");

        assertEquals(2, result.size());
        verify(candidateRepository).findByElectionId("election-1");
    }

    @Test
    void shouldReturnEmptyListWhenNoCandidatesFound() {
        when(candidateRepository.findByElectionId("election-1"))
                .thenReturn(Collections.emptyList());

        List<Candidate> result = candidateService.getCandidatesByElectionId("election-1");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnCandidateById() {
        Candidate candidate = new Candidate();
        candidate.setId("1");

        when(candidateRepository.findById("1"))
                .thenReturn(Optional.of(candidate));

        Candidate result = candidateService.getCandidateById("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void shouldThrowExceptionWhenCandidateNotFound() {
        when(candidateRepository.findById("1"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                candidateService.getCandidateById("1")
        );

        assertEquals("Candidate not found", exception.getMessage());
    }

}