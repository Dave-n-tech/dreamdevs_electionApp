package org.electionapp.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VoteRequest {
    private UUID voterId;
    private String candidateId;
    private String electionId;
}
