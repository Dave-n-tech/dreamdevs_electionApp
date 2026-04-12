package org.electionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ElectionResultResponse {
    private String candidateId;
    private String candidateName;
    private long voteCount;
}