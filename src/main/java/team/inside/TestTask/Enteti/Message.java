package team.inside.TestTask.Enteti;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "counter", nullable = false)
    private Long counter;
    @Column
    private Long id;
    @Column
    private String message;
    @Column(columnDefinition = "TIMESTAMP")
    private Date date;


    public Message() {
    }

    public Message(Long id, String message, Date date) {
        this.id = id;
        this.message = message;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCounter() {
        return counter;
    }
}
