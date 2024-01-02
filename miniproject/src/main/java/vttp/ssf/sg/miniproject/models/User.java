package vttp.ssf.sg.miniproject.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class User {

    @NotEmpty(message="Please enter a username")
    @Size(min=5, max=30, message = "Name must be minimum 2 characters and maximum 30 characters")
    private String username;

    @Pattern(regexp="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$", message=" Min 8 characters, at least 1 uppercase, 1 lowercase & 1 digit ")
    @NotEmpty(message="Please enter a password")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}