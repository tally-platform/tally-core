package org.thomaschen.tally.model;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import javax.persistence.Entity;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "messages")
@EntityListeners(AuditingEntityListener.class)
@EnableScheduling
@JsonIgnoreProperties(value = {"createdAt", "owner"},
        allowGetters = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "msgID")
public class Message {

    /**
     * Unique identifier for message.
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID msgID;

    /**
     * Creation Date/Time of the task.
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Calendar createdAt;

    /**
     * Message body
     */
    @NotBlank
    private String message;

    /**
     * author username
     */
    private String author;

    /**
     * board that owns the message
     */
    @ManyToOne
    private Board owner;

    /**
     * Number of votes the message has
     */
    private Integer votes = 0;

    /**
     * No Param Constructor
     */
    public Message() {

    }

    /**
     * Constructor
     * @param message the message body
     * @param author the author of the message
     */
    public Message(String message, String author, Board owner) {
        this.msgID = UUID.randomUUID();

        this.createdAt = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        this.message = message;
        this.author = author;
        this.owner = owner;
        this.votes = 0;
    }

    /**
     * Get age in seconds of message
     * @return the age of the message
     */
    public Long getAge() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return ChronoUnit.SECONDS.between(createdAt.toInstant(), now.toInstant());
    }

    /**
     * Accessor method for owner field
     * @return the board that owns this message
     */
    public Board getBoard() {
        return owner;
    }

    /**
     * Increase voteCount by 1
     */
    public void upvote() {
        this.votes++;
    }

    /**
     * Decrease voteCount by 1
     */
    public void downvote() {
        this.votes--;
    }

    /**
     * Accessor method for votes field
     * @return the number of votes for this message
     */
    public Integer getVotes() {
        return this.votes;
    }

    /**
     * Accessor method for msgID field
     * @return the msgID of the message
     */
    public UUID getMsgID() {
        return msgID;
    }

    /**
     * Equals method using author + message
     * @param o Message object to be compared with
     * @return whether the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(message, message1.message) &&
                Objects.equals(author, message1.author);
    }

    /**
     * Generates hashcode of object
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(message, author);
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgID=" + msgID +
                ", message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", votes=" + votes +
                '}';
    }

    public void setMsgID(UUID msgID) {
        this.msgID = msgID;
    }

    @JsonIgnore
    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Board getOwner() {
        return owner;
    }

    public void setOwner(Board owner) {
        this.owner = owner;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}
