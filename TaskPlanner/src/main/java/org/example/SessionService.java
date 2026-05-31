package org.example;

public class SessionService {

    private User currentUser;

    public void login(User user) {
        if (user == null) {
            throw new NullPointerException("user must not be null");
        }
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
    }
}
