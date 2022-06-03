package team.inside.MessageApplication.Enteties;

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
    private Date createDate;

    public Token() {}

    public Token(Long id, String token, Date date) {
        this.id = id;
        this.token = token;
        this.createDate = date;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getDate() {
        return createDate;
    }

    public void setDate(Date date) {
        this.createDate = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
