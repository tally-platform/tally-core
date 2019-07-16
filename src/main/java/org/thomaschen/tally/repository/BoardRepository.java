package org.thomaschen.tally.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thomaschen.tally.model.Board;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

    public List<Board> findByBoardIDAndStreamer(UUID id, String streamer);
    public List<Board> findByStreamer(String streamer);
}
