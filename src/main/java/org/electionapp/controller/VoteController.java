package org.electionapp.controller;

import org.electionapp.dto.VoteRequest;
import org.electionapp.model.Vote;
import org.electionapp.model.Voter;
import org.electionapp.service.SessionService;
import org.electionapp.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final SessionService sessionService;

    @PostMapping("/elections/{electionId}")
    public ResponseEntity<Vote> castVote(
            @PathVariable("electionId") String electionId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody VoteRequest request) {

        String sessionId = authHeader.replace("Bearer ", "");
        Voter voter = sessionService.getVoter(sessionId);

        if (voter == null) {
            throw new RuntimeException("Unauthorized");
        }

        request.setElectionId(electionId);
        request.setVoterId(voter.getVotingId());
        return new ResponseEntity<>(voteService.castVote(request), HttpStatus.CREATED);
    }

    @GetMapping("/elections/{electionId}")
    public ResponseEntity<List<Vote>> getVotesByElectionId(@PathVariable String electionId) {
        return new ResponseEntity<>(voteService.getVotesByElectionId(electionId), HttpStatus.OK);
    }

    @GetMapping("/candidates/{candidateId}")
    public ResponseEntity<List<Vote>> getVotesByCandidateId(@PathVariable String candidateId) {
        return new ResponseEntity<>(voteService.getVotesByCandidateId(candidateId), HttpStatus.OK);
    }
}
