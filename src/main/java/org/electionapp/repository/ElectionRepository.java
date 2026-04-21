package org.electionapp.repository;

import org.electionapp.model.Election;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectionRepository extends MongoRepository<Election, String> {
    List<Election> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<Election> findByStartDateAfter(LocalDateTime start);
}
