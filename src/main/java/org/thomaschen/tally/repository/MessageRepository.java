package org.thomaschen.tally.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thomaschen.tally.model.Board;
import org.thomaschen.tally.model.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    public List<Message> findByOwner(Board board);
    public List<Message> findByAuthor(String author);

}
