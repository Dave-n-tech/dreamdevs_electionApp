package org.electionapp.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionData {
    private Voter voter;
    private LocalDateTime expiresAt;

    public SessionData(Voter voter, LocalDateTime expiresAt) {
        this.voter = voter;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

}
