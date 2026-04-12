package org.electionapp.service;

import org.electionapp.dto.VoterRequest;
import org.electionapp.model.Voter;
import org.electionapp.repository.VoterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoterServiceTest {

    @Mock
    private VoterRepository voterRepository;

    @InjectMocks
    private VoterService voterService;

    @Test
    void shouldRegisterVoterSuccessfully() {
        VoterRequest request = new VoterRequest();
        request.setName("  John Doe  ");
        request.setEmail("john@email.com");

        when(voterRepository.save(any(Voter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Voter result = voterService.register(request);

        assertNotNull(result);
        assertEquals("john doe", result.getName());
        assertEquals("john@email.com", result.getEmail());
        assertNotNull(result.getVotingId());

        verify(voterRepository).save(any(Voter.class));

    }

    @Test
    void shouldThrowWhenVoterAlreadyExists() {
        VoterRequest request = new VoterRequest();
        request.setVotingId(UUID.randomUUID());
        request.setName("John Doe");
        request.setEmail("john@email.com");

        when(voterRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voterService.register(request)
        );

        assertEquals("Voter already exists", ex.getMessage());
        verify(voterRepository, never()).save(any());
    }

    @Test
    void shouldGenerateNewVotingIdOnRegistration() {
        VoterRequest request = new VoterRequest();
        request.setName("Test User");
        request.setEmail("test@mail.com");

        when(voterRepository.save(any(Voter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Voter> captor = ArgumentCaptor.forClass(Voter.class);
        voterService.register(request);

        verify(voterRepository).save(captor.capture());
        Voter saved = captor.getValue();
        assertNotNull(saved.getVotingId());
    }

    @Test
    void shouldReturnAllVoters() {
        List<Voter> voters = List.of(new Voter(), new Voter());

        when(voterRepository.findAll()).thenReturn(voters);

        List<Voter> result = voterService.getAllVoters();

        assertEquals(2, result.size());
        verify(voterRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoVotersExist() {
        when(voterRepository.findAll()).thenReturn(List.of());

        List<Voter> result = voterService.getAllVoters();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnVoterByVotingId() {
        UUID id = UUID.randomUUID();
        Voter voter = new Voter();
        voter.setVotingId(id);

        when(voterRepository.findByVotingId(id))
                .thenReturn(Optional.of(voter));

        Optional<Voter> result = voterService.getByVotingId(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getVotingId());
    }

    @Test
    void shouldReturnEmptyWhenVoterNotFound() {
        UUID id = UUID.randomUUID();

        when(voterRepository.findByVotingId(id))
                .thenReturn(Optional.empty());

        Optional<Voter> result = voterService.getByVotingId(id);

        assertTrue(result.isEmpty());
    }
}