package team.inside.TestTask.Enteti;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column
    private String token;
    @Column(name = "date", columnDefinition = "TIMESTAMP")
    private Date date;

    public Token() {}

    public Token(Long id, String token, Date date) {
        this.id = id;
        this.token = token;
        this.date = date;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
