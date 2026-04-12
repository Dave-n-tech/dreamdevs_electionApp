package org.electionapp.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class VoteRequest {
    @NonNull
    private UUID voterId;
    @NonNull
    private String candidateId;
    @NonNull
    private String electionId;

    public VoteRequest() {

    }
}
