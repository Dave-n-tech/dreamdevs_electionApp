package org.electionapp.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VoterRequest {
    private String name;
    private UUID votingId;
    private String email;
}
