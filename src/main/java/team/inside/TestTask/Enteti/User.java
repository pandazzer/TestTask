package team.inside.TestTask.Enteti;

import javax.persistence.*;

@Entity
@Table(name = "Users_DB")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column
    private String users;
    @Column
    private String password;

    public String getUser() {
        return users;
    }

    public void setUser(String user) {
        this.users = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

}
