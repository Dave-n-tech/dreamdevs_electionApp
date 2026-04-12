package org.electionapp.service;

import org.electionapp.dto.CreateCandidateRequest;
import org.electionapp.exception.BadRequestException;
import org.electionapp.exception.ResourceNotFoundException;
import org.electionapp.model.Candidate;
import org.electionapp.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public Candidate registerCandidate(CreateCandidateRequest request) {

        if(candidateRepository.existsByNameAndElectionId(request.getName(), request.getElectionId())){
            throw new BadRequestException("Candidate already registered");
        }

        Candidate newCandidate = new Candidate();
        newCandidate.setName(request.getName().trim().toLowerCase());
        newCandidate.setParty(request.getParty());
        newCandidate.setElectionId(request.getElectionId());

        return candidateRepository.save(newCandidate);
    }

    public List<Candidate> getCandidatesByElectionId(String electionId) {
        return candidateRepository.findByElectionId(electionId);
    }

    public Candidate getCandidateById(String id){
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
    }
}
