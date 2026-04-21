package org.electionapp.service;

import org.electionapp.model.SessionData;
import org.electionapp.model.Voter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();
    private static final long SESSION_DURATION_MINUTES = 30;

    public String createSession(Voter voter) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(SESSION_DURATION_MINUTES);

        sessions.put(sessionId, new SessionData(voter, expiry));
        return sessionId;
    }

    public Voter getVoter(String sessionId) {
        SessionData session = sessions.get(sessionId);

        if (session == null || session.isExpired()) {
            sessions.remove(sessionId);
            return null;
        }

        return session.getVoter();
    }
}
