package org.electionapp.controller;

import org.electionapp.dto.VoteRequest;
import org.electionapp.model.Vote;
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

    @PostMapping("/elections/{electionId}")
    public ResponseEntity<Vote> castVote(
            @PathVariable("electionId") String electionId,
            @RequestBody VoteRequest request) {

        request.setElectionId(electionId);
        return new ResponseEntity<>(voteService.castVote(request), HttpStatus.OK);
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
