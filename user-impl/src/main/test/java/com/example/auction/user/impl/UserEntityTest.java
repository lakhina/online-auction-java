package com.example.auction.user.impl;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import org.junit.*;

import com.example.auction.user.impl.UserCommand.*;
import com.example.auction.user.impl.UserEvent.*;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class UserEntityTest {

    private static ActorSystem system;
    private PersistentEntityTestDriver<UserCommand, UserEvent, PUser> driver;

    @BeforeClass
    public static void startActorSystem() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void shutdownActorSystem() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    private final UUID id = UUID.randomUUID();
    private final String name = "admin";
    private final String email = "admin@gmail.com";
    private final String password="admin";

    private final PUser user  = new PUser(id, name, email);

    @Before
    public void createTestDriver() {
        driver = new PersistentEntityTestDriver<>(system, new UserEntity(), id.toString());
    }

    @After
    public void noIssues() {
        if (!driver.getAllIssues().isEmpty()) {
            driver.getAllIssues().forEach(System.out::println);
            fail("There were issues " + driver.getAllIssues().get(0));
        }
    }

    @Test
    public void testCreateUser() {
        PersistentEntityTestDriver.Outcome<UserEvent, PUser> outcome = driver.run(new CreateUser(user));
        assertThat(outcome.state().getUser(), equalTo(Optional.of(user)));
        assertThat(outcome.events(), hasItem(new UserCreated(user));
    }
    @Test
    public void testRejectDuplicateCreate() {
        assertEquals(PersistentEntity.InvalidCommandException.class, outcome.getReplies().get(0).getClass());
        assertEquals(Collections.emptyList(), outcome.events());
        assertEquals(Collections.emptyList(), driver.getAllIssues());
    }
}
