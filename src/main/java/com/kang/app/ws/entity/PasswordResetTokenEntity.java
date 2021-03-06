package com.kang.app.ws.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "password_rest_tokens")
public class PasswordResetTokenEntity implements Serializable {

    private static final long serialVersionUID = 8620412534406753549L;

    @Id
    @GeneratedValue
    private long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
