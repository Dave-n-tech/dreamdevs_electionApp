package org.electionapp.controller;

import org.electionapp.dto.VoterRequest;
import org.electionapp.exception.ResourceNotFoundException;
import org.electionapp.model.Voter;
import org.electionapp.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/voters")
@RequiredArgsConstructor
public class VoterController {

    private final VoterService voterService;

    @PostMapping
    public ResponseEntity<Voter> register(@RequestBody VoterRequest voter) {
        return new ResponseEntity<>(voterService.register(voter), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Voter>> getAll() {
        return new ResponseEntity<>(voterService.getAllVoters(), HttpStatus.OK);
    }

    @GetMapping("/{votingId}")
    public ResponseEntity<Voter> getVoterByVotingId(@PathVariable UUID votingId) {
        return voterService.getByVotingId(votingId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));
    }
}
