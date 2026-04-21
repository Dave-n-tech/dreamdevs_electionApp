package org.electionapp.controller;

import org.electionapp.dto.CreateElectionRequest;
import org.electionapp.dto.ElectionFilterRequest;
import org.electionapp.dto.ElectionResultResponse;
import org.electionapp.model.Election;
import org.electionapp.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {
    @Autowired
    private final ElectionService electionService;

    @GetMapping
    public ResponseEntity<List<Election>> getAll(
            @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) LocalDateTime endDate
    ) {
        ElectionFilterRequest filterRequest = new ElectionFilterRequest(startDate, endDate);
        return ResponseEntity.ok(
                electionService.getAllElections(filterRequest)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getById(@PathVariable("id") String id){
        return new ResponseEntity<>(electionService.getElectionById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Election> create(@RequestBody CreateElectionRequest request) {
        Election election = electionService.createElection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(election);
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<List<ElectionResultResponse>> results(@PathVariable("id") String id) {
        return new ResponseEntity<>(electionService.getResults(id), HttpStatus.OK);
    }
}