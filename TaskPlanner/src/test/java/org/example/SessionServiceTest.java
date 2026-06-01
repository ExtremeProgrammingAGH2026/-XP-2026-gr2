package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionServiceTest {

    private SessionService sessionService;
    private User user;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();
        user = new User("1", "anna@example.com", "Anna", "secret");
    }

    @Test
    void shouldNotBeLoggedInInitially() {
        assertFalse(sessionService.isLoggedIn());
        assertNull(sessionService.getCurrentUser());
    }

    @Test
    void shouldBeLoggedInAfterLogin() {
        sessionService.login(user);

        assertTrue(sessionService.isLoggedIn());
    }

    @Test
    void shouldReturnCurrentUserAfterLogin() {
        sessionService.login(user);

        assertSame(user, sessionService.getCurrentUser());
    }

    @Test
    void shouldThrowWhenLoginWithNullUser() {
        assertThrows(NullPointerException.class, () -> sessionService.login(null));
    }

    @Test
    void shouldNotBeLoggedInAfterLogout() {
        sessionService.login(user);

        sessionService.logout();

        assertFalse(sessionService.isLoggedIn());
        assertNull(sessionService.getCurrentUser());
    }

    @Test
    void shouldReplaceCurrentUserWhenLoggingInAgain() {
        User other = new User("2", "bob@example.com", "Bob", "pass");
        sessionService.login(user);

        sessionService.login(other);

        assertSame(other, sessionService.getCurrentUser());
    }
}
