package org.thomaschen.tally;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thomaschen.tally.model.Board;
import org.thomaschen.tally.repository.BoardRepository;
import org.thomaschen.tally.repository.MessageRepository;

import java.util.List;


@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    BoardRepository boardRepository;

    @Scheduled(fixedRate = 5000)
    public void refreshBoards() {
        log.info("Refreshing Boards...");
        List<Board> allBoards = boardRepository.findAll();

        for (Board board : allBoards) {
            board.refreshBoard(boardRepository);
        }
    }
}
