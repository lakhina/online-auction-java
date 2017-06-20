package com.example.auction.user.impl;

import com.example.auction.user.api.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public interface UserCommand extends Jsonable {
    @Value
    final class CreateUser implements UserCommand, PersistentEntity.ReplyType<User> {
        private final String name;
        private final String email;
        private final String password;

        @JsonCreator
        public CreateUser(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }
        public static String hashPassword(String password_plaintext) {
            String salt = BCrypt.gensalt(12);
            String hashed_password = BCrypt.hashpw(password_plaintext, salt);

            return(hashed_password);
        }
        public static boolean checkPassword(String password_plaintext, String stored_hash) {
            boolean password_verified = false;

            if(null == stored_hash)
                throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

            password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

            return(password_verified);
        }
    }

    enum GetUser implements UserCommand, PersistentEntity.ReplyType<Optional<PUser>> {
        INSTANCE
    }
}
