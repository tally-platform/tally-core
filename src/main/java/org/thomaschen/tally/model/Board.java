package org.thomaschen.tally.model;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.thomaschen.tally.repository.BoardRepository;
import org.thomaschen.tally.repository.MessageRepository;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.*;

@Entity
@Table(name = "boards")
@EntityListeners(AuditingEntityListener.class)
@EnableScheduling
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "boardID")
public class Board {

    /**
     * Unique identifier for task.
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID boardID;

    /**
     * Streamer who owns board
     */
    private String streamer;

    /**
     * Title of the board.
     */
    @NotBlank
    private String title;

    /**
     * Creation Date/Time of the task.
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Calendar createdAt;

    /**
     * Last Modified Date/time
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Calendar updatedAt;

    /**
     * Count threshold for task to pop
     */
    @NotNull(message = "The above field must not be omitted.")
    @Min(value = 0, message = "The value must be positive")
    private Integer countThreshold;

    /**
     * Maximum number of messages to add to board
     */
    @NotNull(message = "The above field must not be omitted.")
    @Min(value = 0, message = "The value must be positive")
    private Integer maxMessages;

    /**
     * Time threshold for expiring inactive messages in seconds
     */
    @NotNull(message = "The above field must not be omitted.")
    @Min(value = 0L, message = "The value must be positive")
    private Long timeThreshold;

    /**
     * Queue of messages that have surpassed threshhold
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> messageList;

    /**
     * Hashmap of all messages currently on Board
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "owner", fetch = FetchType.EAGER)
    @MapKey(name = "msgID")
    private Map<UUID, Message> messageBoard;

    /**
     * No Param Constructor
     */
    public Board() {

    }

    /**
     * Constructor for Board class.
     * @param streamer the username of the streamer who owns the board
     * @param title the title of the board
     * @param countThreshold the count threshhold of the board
     * @param timeThreshold the time threshhold of the board
     */
    public Board(String streamer, String title, Integer countThreshold, Long timeThreshold, Integer maxMessages) {
        this.boardID = UUID.randomUUID();

        this.title = title;
        this.streamer = streamer;
        this.countThreshold = countThreshold;
        this.timeThreshold = timeThreshold;
        this.maxMessages = maxMessages;

        this.createdAt = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        this.updatedAt = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        this.messageList = new LinkedList<String>();
        this.messageBoard = new HashMap<>();
    }

    /**
     * Refreshes the board removing expired messages, and adding approved messages to message stack
     */
    @PersistenceContext
    public void refreshBoard(BoardRepository br) {
        Iterator it = messageBoard.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Message msg = (Message) pair.getValue();
            UUID key = (UUID) pair.getKey();

            if (msg.getAge() > timeThreshold) {
                messageBoard.remove(key);
                br.save(this);

                System.out.println(key + " EXPIRED: " + msg.getMessage());
                continue;
            }

            if (msg.getVotes() > countThreshold) {
                messageBoard.remove(key);
                messageList.add("{\"author\": " + "\"" + msg.getAuthor() + "\",\n\"message\": " + "\"" + msg.getMessage() + "\"}");
                br.save(this);
                System.out.println(key + " PASSED: " + msg.getMessage());
            }
        }
    }

    /**
     * Adds message to board
     * @param msg the message to be added
     */
    public void addMessage(Message msg) {
        if (messageBoard.size() < maxMessages && !messageBoard.containsKey(msg)) {
            messageBoard.put(msg.getMsgID(), msg);
        }
        //TODO: Throw Exception if fail to add message
    }

    /**
     * Pops one message off the approved queue and returns it
     * @return the message at the top of the stack
     */
    public String anounceMessage() {
        String lastMsg = messageList.get(0);
        messageList.remove(0);
        return lastMsg;
    }

    /**
     * Upvotes a specified message
     * @param id the boardID of message to be upvoted
     */
    public void upvoteMsg(UUID id) {
        Message msg = messageBoard.get(id);
        msg.upvote();
        messageBoard.put(id, msg);
    }

    /**
     * Downvotes a specified message
     * @param id the boardID of message to be upvoted
     */
    public void downvoteMsg(UUID id) {
        Message msg = messageBoard.get(id);
        msg.downvote();
        messageBoard.put(id, msg);
    }

    /**
     * Sets the streamer of the board
     * @param streamer the streamer's username
     */
    public void setStreamer(String streamer) {
        this.streamer = streamer;
    }

    /**
     * Accessor method for UUID
     * @return the boardID of the board
     */
    public UUID getBoardID() {
        return boardID;
    }

    @Override
    public String toString() {
        return "Board{" +
                "boardID=" + boardID +
                ", streamer='" + streamer + '\'' +
                ", title='" + title + '\'' +
                ", countThreshold=" + countThreshold +
                ", maxMessages=" + maxMessages +
                ", timeThreshold=" + timeThreshold +
                ", messageList=" + messageList +
                '}';
    }

    public void setBoardID(UUID boardID) {
        this.boardID = boardID;
    }

    public String getStreamer() {
        return streamer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnore
    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public Calendar getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Calendar updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCountThreshold() {
        return countThreshold;
    }

    public void setCountThreshold(Integer countThreshold) {
        this.countThreshold = countThreshold;
    }

    public Integer getMaxMessages() {
        return maxMessages;
    }

    public void setMaxMessages(Integer maxMessages) {
        this.maxMessages = maxMessages;
    }

    public Long getTimeThreshold() {
        return timeThreshold;
    }

    public void setTimeThreshold(Long timeThreshold) {
        this.timeThreshold = timeThreshold;
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }

    public Map<UUID, Message> getMessageBoard() {
        return messageBoard;
    }

    public void setMessageBoard(Map<UUID, Message> messageBoard) {
        this.messageBoard = messageBoard;
    }
}
