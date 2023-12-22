package vttp.ssf.sg.miniproject.models;

import jakarta.validation.constraints.NotEmpty;

public class User {

    @NotEmpty(message="Please enter your name")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
}