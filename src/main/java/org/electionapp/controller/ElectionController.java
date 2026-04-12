package org.electionapp.controller;

import org.electionapp.dto.CreateElectionRequest;
import org.electionapp.dto.ElectionFilterRequest;
import org.electionapp.dto.ElectionResultResponse;
import org.electionapp.model.Election;
import org.electionapp.model.ElectionStatus;
import org.electionapp.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping
    public ResponseEntity<List<Election>> getAll(
            @RequestParam(name = "status", required = false) ElectionStatus status,
            @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) LocalDateTime endDate
    ) {
        ElectionFilterRequest filterRequest = new ElectionFilterRequest(status, startDate, endDate);
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

    @PostMapping("/{id}/start")
    public ResponseEntity<Election> start(@PathVariable("id") String id) {
        return new ResponseEntity<>(electionService.startElection(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<Election> end(@PathVariable("id") String id) {
        return new ResponseEntity<>(electionService.endElection(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<List<ElectionResultResponse>> results(@PathVariable("id") String id) {
        return new ResponseEntity<>(electionService.getResults(id), HttpStatus.OK);
    }
}