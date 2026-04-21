package org.electionapp.controller;

import org.electionapp.dto.CreateCandidateRequest;
import org.electionapp.model.Candidate;
import org.electionapp.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping("/elections/{electionId}")
    public ResponseEntity<Candidate> register(@PathVariable("electionId") String electionId, @RequestBody CreateCandidateRequest candidate) {
        candidate.setElectionId(electionId);
        return new ResponseEntity<>(candidateService.registerCandidate(candidate), HttpStatus.CREATED);
    }

    @GetMapping("/elections/{electionId}")
    public ResponseEntity<List<Candidate>> getCandidatesByElectionId(@PathVariable String electionId) {
        return new ResponseEntity<>(candidateService.getCandidatesByElectionId(electionId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getById(@PathVariable String id) {
        return new ResponseEntity<>(candidateService.getCandidateById(id), HttpStatus.OK);
    }

}
