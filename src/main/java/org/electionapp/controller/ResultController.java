package org.electionapp.controller;

import lombok.RequiredArgsConstructor;
import org.electionapp.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
public class ResultController {

    private final VoteService voteService;

    @GetMapping("/{electionId}")
    public ResponseEntity<Map<String, Long>> getResults(
            @PathVariable String electionId
    ) {
        Map<String, Long> results = voteService.getResults(electionId);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}