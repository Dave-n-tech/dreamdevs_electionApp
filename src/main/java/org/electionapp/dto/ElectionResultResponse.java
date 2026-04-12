package org.electionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class ElectionResultResponse {
    @NonNull
    private String candidateId;
    private String candidateName;
    private long voteCount;

    public ElectionResultResponse() {
    }
}