package com.example.auction.user.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Optional;
import java.util.UUID;

@Value
public final class PUser {

    private final UUID id;
    private final String name;
    private final String email;
    private final String passwordHash;

    @JsonCreator
    public PUser(@JsonProperty("id") UUID id, @JsonProperty("name") String name, @JsonProperty("email") String email, @JsonProperty("password") String passwordHash) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
    }

}
