package com.example.auction.user.impl;

import akka.NotUsed;
import com.datastax.driver.core.Row;
import com.example.auction.pagination.PaginatedSequence;
import com.example.auction.user.api.User;
import com.example.auction.user.api.UserRegistration;
import com.example.auction.user.api.UserService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PSequence;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class UserServiceImpl implements UserService {

    private final PersistentEntityRegistry registry;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private final UserRepository userRepository;

    @Inject
    public UserServiceImpl(PersistentEntityRegistry registry, UserRepository userRepository) {
        this.registry = registry;
        this.userRepository = userRepository;

        registry.register(PUserEntity.class);
    }

    @Override
    public ServiceCall<UserRegistration, User> createUser() {
        return user -> {
            CompletionStage<PSequence<User>> sequence = userRepository.selectUsersByEmail(user.getEmail());
           return sequence.thenApply(users -> {
                Optional<User> userByEmail = users.stream().filter(req ->
                        user.getEmail().equals(req.getEmail())
                ).findFirst();
                if(!userByEmail.isPresent()) {
                    UUID uuid = UUID.randomUUID();
                    String password = PUserCommand.hashPassword(user.getPassword());
                    PUser createdUser = new PUser(uuid, user.getName(), user.getEmail(), password);
                    entityRef(uuid)
                            .ask(new PUserCommand.CreatePUser(user.getName(), user.getEmail(), password))
                            .thenApply(done -> Mappers.toApi(Optional.ofNullable(createdUser)));

                }

                else{
                        throw new NotFound("User with this email id already exists");
                    }
               return null;
                });

        };
    }

    @Override
    public ServiceCall<NotUsed, User> getUser(UUID userId) {

        return request ->

                entityRef(userId)
                        .ask(PUserCommand.GetPUser.INSTANCE)
                        .thenApply(maybeUser -> {
                            User user = Mappers.toApi(((Optional<PUser>) maybeUser));
                            return user;

                        });

    }

    @Override
    public ServiceCall<NotUsed, PaginatedSequence<User>> getUsers(Optional<Integer> pageNo, Optional<Integer> pageSize) {
        return req -> userRepository.getUsers(pageNo.orElse(0), pageSize.orElse(DEFAULT_PAGE_SIZE));
    }

    private PersistentEntityRef<PUserCommand> entityRef(UUID id) {
        return entityRef(id.toString());
    }

    private PersistentEntityRef<PUserCommand> entityRef(String id) {
        return registry.refFor(PUserEntity.class, id);
    }
}
