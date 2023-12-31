package vttp.ssf.sg.miniproject.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class User {

    @NotEmpty(message="Please enter your name")
    @Size(min=5, max=30, message = "Name must be minimum 2 characters and maximum 30 characters")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}