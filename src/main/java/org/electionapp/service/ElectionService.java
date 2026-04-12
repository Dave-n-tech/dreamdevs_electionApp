package org.electionapp.service;

import org.electionapp.dto.CreateElectionRequest;
import org.electionapp.dto.ElectionFilterRequest;
import org.electionapp.dto.ElectionResultResponse;
import org.electionapp.exception.ResourceNotFoundException;
import org.electionapp.model.Candidate;
import org.electionapp.model.Election;
import org.electionapp.model.ElectionStatus;
import org.electionapp.model.Vote;
import org.electionapp.repository.CandidateRepository;
import org.electionapp.repository.ElectionRepository;
import org.electionapp.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;

    public List<Election> getAllElections(ElectionFilterRequest filterRequest) {

        boolean hasStatus = filterRequest.getStatus() != null;
        boolean hasStart = filterRequest.getStartDate() != null;
        boolean hasEnd = filterRequest.getEndDate() != null;

        if (!hasStatus && !hasStart && !hasEnd) {
            return electionRepository.findAll();
        }

        ElectionStatus electionStatus = null;
        if (hasStatus) {
            electionStatus = filterRequest.getStatus();
        }

        if (hasStatus && hasStart && hasEnd){
            return electionRepository.findByStatusAndStartDateBetween(
                    electionStatus,
                    filterRequest.getStartDate(),
                    filterRequest.getEndDate()
            );
        }

        if (hasStatus) {
            return electionRepository.findByStatus(electionStatus);
        }

        if (hasStart && hasEnd){
            return electionRepository.findByStartDateBetween(
                    filterRequest.getStartDate(),
                    filterRequest.getEndDate()
            );
        }

        if (hasStart) {
            return electionRepository.findByStartDateAfter(filterRequest.getStartDate());
        }

        return electionRepository.findAll();
    }

    public Election getElectionById(String id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));
    }

    public Election createElection(CreateElectionRequest request) {
        if (request.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (request.getStartDate().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Election start date cannot be in the past");
        }


        Election election = new Election();
        election.setName(request.getName());
        election.setDescription(request.getDescription());
        election.setStartDate(request.getStartDate());
        election.setEndDate(request.getEndDate());
        election.setStatus(request.getStatus());

        return electionRepository.save(election);
    }

    public Election startElection(String id) {
        Election election = getElectionById(id);

        if (election.getStatus() != ElectionStatus.UPCOMING) {
            throw new IllegalArgumentException("Only upcoming elections can be started");
        }

        election.setStatus(ElectionStatus.ONGOING);
        return electionRepository.save(election);
    }

    public Election endElection(String id) {
        Election election = getElectionById(id);

        if (election.getStatus() != ElectionStatus.ONGOING) {
            throw new IllegalArgumentException("Only ongoing elections can be ended");
        }

        election.setStatus(ElectionStatus.ENDED);
        return electionRepository.save(election);
    }

    public List<ElectionResultResponse> getResults(String electionId) {

        Election election = getElectionById(electionId);

        if (election.getStatus() != ElectionStatus.ENDED) {
            throw new RuntimeException("Election must be ended to view results");
        }

        List<Vote> votes = voteRepository.findByElectionId(electionId);

        Map<String, Long> voteCount = votes.stream()
                .collect(Collectors.groupingBy(
                        Vote::getCandidateId,
                        Collectors.counting()
                ));

        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);

        List<ElectionResultResponse> list = new ArrayList<>();
        for (Candidate candidate : candidates) {
            ElectionResultResponse electionResultResponse = new ElectionResultResponse(
                    candidate.getId(),
                    candidate.getName(),
                    voteCount.getOrDefault(candidate.getId(), 0L)
            );
            list.add(electionResultResponse);
        }
        return list;
    }

}
