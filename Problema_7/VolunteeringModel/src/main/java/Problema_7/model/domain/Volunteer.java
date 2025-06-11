package Problema_7.model.domain;

import Problema_7.model.domain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.Objects;


@jakarta.persistence.Entity
@Table(name = "volunteer")
public class Volunteer extends Entity<Integer> {
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

    public Volunteer() {
    }

    public Volunteer(int id, String username, String password) {
        this.setId(id);
        this.username = username;
        this.password = password;
    }

    public Volunteer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volunteer volunteer = (Volunteer) o;
        return Objects.equals(username, volunteer.username) && Objects.equals(password, volunteer.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                "username='" + username;
    }
}
