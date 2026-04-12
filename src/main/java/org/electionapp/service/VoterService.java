package org.electionapp.service;

import org.electionapp.dto.VoterRequest;
import org.electionapp.model.Voter;
import org.electionapp.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoterService {
    private final VoterRepository voterRepository;

    public Voter register(VoterRequest newVoter) {

        if(voterRepository.existsByEmail(newVoter.getEmail())){
            throw new IllegalArgumentException("Voter already exists");
        }

        Voter voter = new Voter();
        voter.setName(newVoter.getName().trim().toLowerCase());
        voter.setEmail(newVoter.getEmail());
        voter.setVotingId(UUID.randomUUID());

        return voterRepository.save(voter);
    }

    public List<Voter> getAllVoters() {

        return voterRepository.findAll();
    }

    public Optional<Voter> getByVotingId(UUID votingId) {
        return voterRepository.findByVotingId(votingId);
    }
}
