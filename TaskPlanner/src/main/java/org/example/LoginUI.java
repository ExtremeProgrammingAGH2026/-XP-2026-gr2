package org.example;

import java.util.Scanner;

public class LoginUI {

    private static final int MAX_ATTEMPTS = 3;

    private final AuthService authService;

    public LoginUI(AuthService authService) {
        this.authService = authService;
    }

    public User login(Scanner scanner) {
        System.out.println("=== Login ===");
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            User user = authService.authenticateUser(email, password);
            if (user != null) {
                System.out.println("Logged in as: " + user.getName());
                return user;
            }

            attempts++;
            int remaining = MAX_ATTEMPTS - attempts;
            if (remaining > 0) {
                System.out.println("Invalid email or password. " + remaining + " attempt(s) remaining.");
            }
        }

        System.out.println("Too many failed attempts. Exiting.");
        return null;
    }
}
