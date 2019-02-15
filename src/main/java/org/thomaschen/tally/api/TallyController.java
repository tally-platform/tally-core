package org.thomaschen.tally.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thomaschen.tally.exceptions.ResourceNotFoundException;
import org.thomaschen.tally.model.Board;
import org.thomaschen.tally.model.Message;
import org.thomaschen.tally.repository.BoardRepository;
import org.thomaschen.tally.repository.MessageRepository;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TallyController {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    MessageRepository messageRepository;


    @GetMapping("/")
    public String index() {
        return "Hello, Welcome to the Tally API";
    }

    @GetMapping("/login")
    public ResponseEntity login(Principal principal) {
        return ResponseEntity.ok("Successfully Authenticated: " + principal.getName());
    }

    // Get all Boards
    @GetMapping("/boards")
    public List<Board> getAllBoards(Principal principal) {
        return boardRepository.findByStreamer(principal.getName());
    }

    // Create a Board
    @PostMapping("/boards")
    public Board createBoard(@Valid @RequestBody Board board, Principal principal) {
        board.setStreamer(principal.getName());
        return boardRepository.save(board);
    }

    // Update a Task
    @PutMapping("/boards/{id}")
    public Board updateBoard(@PathVariable(value = "id") UUID boardID,
                           @Valid @RequestBody Board boardDetails) {
        Board board = boardRepository.findById(boardID)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardID));

        board.setTitle(boardDetails.getTitle());
        board.setCountThreshold(boardDetails.getCountThreshold());
        board.setTimeThreshold(boardDetails.getTimeThreshold());
        board.setMaxMessages(boardDetails.getMaxMessages());

        Board updatedBoard = boardRepository.save(board);
        return updatedBoard;
    }

    // Get specific Board
    @GetMapping("/boards/{id}")
    public Board getBoardById(@PathVariable(value = "id") UUID boardID) {
        return boardRepository.findById(boardID)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardID));
    }

    // Delete a Board
    @DeleteMapping("/boards/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable(value = "id") UUID boardID, Principal principal) {
        Board board = boardRepository.findById(boardID)
                .orElseThrow( () -> new ResourceNotFoundException("Board", "id", boardID));

        boardRepository.delete(board);

        return ResponseEntity.ok().build();
    }

    // Add Message to Board
    @PostMapping("/boards/{id}/messages")
    public Message addMessage(@Valid
                                @PathVariable(value = "id") UUID boardID,
                                @RequestBody Message msg, Principal principal) {
        Board board = boardRepository.findById(boardID)
                .orElseThrow( () -> new ResourceNotFoundException("Board", "id", boardID));


        msg.setAuthor(principal.getName());
        msg.setOwner(board);
        Message newMsg = messageRepository.save(msg);

        board.addMessage(newMsg);
        Board updatedBoard = boardRepository.save(board);

        return newMsg;
    }

    // Upvote a Message in a Board
    @PostMapping("/boards/{id}/messages/{msgid}/upvote")
    public Message upvoteMessage(
            @Valid @PathVariable(value = "id") UUID boardID,
            @PathVariable(value = "msgid") UUID msgID) {

        boardRepository.findById(boardID)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardID));

        Message msg = messageRepository.findById(msgID)
                .orElseThrow( () -> new ResourceNotFoundException("Message", "id", msgID));

        msg.upvote();

        Message updatedMsg = messageRepository.save(msg);

        return updatedMsg;
    }

    // Downvote a Message in a Board
    @PostMapping("/boards/{id}/messages/{msgid}/downvote")
    public Message downvoteMessage(
            @Valid @PathVariable(value = "id") UUID boardID,
            @PathVariable(value = "msgid") UUID msgID) {

        boardRepository.findById(boardID)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardID));

        Message msg = messageRepository.findById(msgID)
                .orElseThrow( () -> new ResourceNotFoundException("Message", "id", msgID));

        msg.downvote();

        Message updatedMsg = messageRepository.save(msg);

        return updatedMsg;
    }

    // Get next approved message
    @GetMapping("/boards/{id}/queue")
    public ResponseEntity<String> getNextMessage(@Valid @PathVariable(value = "id") UUID boardID) {
        Board board = boardRepository.findById(boardID)
                .orElseThrow( () -> new ResourceNotFoundException("Board", "id", boardID));

        String nextMsg = board.anounceMessage();
        Board updatedBoard = boardRepository.save(board);

        if (nextMsg == null) {
            return ResponseEntity.ok("");
        }

        return ResponseEntity.ok(nextMsg);
    }








}
