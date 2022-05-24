package team.inside.TestTask.Enteti;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "Message_DB")
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column
    private String message;
    @Column(columnDefinition = "TIMESTAMP")
    private Date date;

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
}
