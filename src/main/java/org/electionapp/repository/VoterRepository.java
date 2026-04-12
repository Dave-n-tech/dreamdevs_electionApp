package org.electionapp.repository;

import org.electionapp.model.Voter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoterRepository extends MongoRepository<Voter, String> {
    Optional<Voter> findByVotingId(UUID votingId);
    boolean existsByEmail(String email);
}
