package org.electionapp.dto;

import jakarta.annotation.Nonnull;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginRequest {
    @Nonnull
    public UUID votingId;
}
