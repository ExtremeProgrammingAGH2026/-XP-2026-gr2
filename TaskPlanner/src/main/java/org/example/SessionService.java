package org.example;

import java.util.Objects;

/**
 * Stores the currently logged-in user for the duration of a session.
 */
public class SessionService {

    private User currentUser;

    /**
     * Sets the given user as the currently logged-in user.
     *
     * @param user user to log in; must not be null
     * @throws NullPointerException if user is null
     */
    public void login(User user) {
        this.currentUser = Objects.requireNonNull(user, "user must not be null");
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Tells whether a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Clears the currently logged-in user.
     */
    public void logout() {
        this.currentUser = null;
    }
}
