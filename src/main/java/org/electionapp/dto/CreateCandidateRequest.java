package org.electionapp.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class CreateCandidateRequest {
    @NonNull
    private String name;
    @NonNull
    private String party;
    @NonNull
    private String electionId;
}
