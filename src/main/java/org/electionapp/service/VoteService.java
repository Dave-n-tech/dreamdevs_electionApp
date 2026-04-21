package org.electionapp.service;

import org.electionapp.dto.VoteRequest;
import org.electionapp.exception.ResourceNotFoundException;
import org.electionapp.model.*;
import org.electionapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoterRepository voterRepository;

    public Vote castVote(VoteRequest request) {

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        if (election.getStartDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Election has not started");
        }

        if (election.getEndDate().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Election has ended");
        }

        Voter voter = voterRepository.findByVotingId(request.getVoterId())
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (!candidate.getElectionId().equals(request.getElectionId())) {
            throw new RuntimeException("Candidate not in this election");
        }

        Vote foundVote = voteRepository.findByVoterIdAndElectionId(
                request.getVoterId(),
                request.getElectionId()
        );

        if (foundVote != null) {
            throw new RuntimeException("Voter has already voted");
        }

        Vote vote = new Vote();
        vote.setVoterId(voter.getVotingId());
        vote.setCandidateId(candidate.getId());
        vote.setElectionId(election.getId());
        vote.setTimestamp(LocalDateTime.now());

        return voteRepository.save(vote);
    }

    public List<Vote> getVotesByElectionId(String electionId){
        return voteRepository.findByElectionId(electionId);
    }

    public List<Vote> getVotesByCandidateId(String candidateId){
        return voteRepository.findByCandidateId(candidateId);
    }
}
