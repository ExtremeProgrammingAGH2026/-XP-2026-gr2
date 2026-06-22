package org.example;

import java.util.Scanner;
import java.util.function.IntSupplier;

public class LoginUI {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;

    private final AuthService authService;
    private final IntSupplier maxAttemptsSupplier;

    public LoginUI(AuthService authService) {
        this(authService, DEFAULT_MAX_ATTEMPTS);
    }

    public LoginUI(AuthService authService, int maxAttempts) {
        this(authService, () -> maxAttempts);
    }

    public LoginUI(AuthService authService, IntSupplier maxAttemptsSupplier) {
        this.authService = authService;
        this.maxAttemptsSupplier = maxAttemptsSupplier;
    }

    public User login(Scanner scanner) {
        System.out.println("=== Login ===");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        int maxAttempts = maxAttemptsSupplier.getAsInt();
        int attempts = 0;
        while (attempts < maxAttempts) {
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            User user = authService.authenticateUser(email, password);
            if (user != null) {
                System.out.println("Logged in as: " + user.getName());
                return user;
            }

            attempts++;
            int remaining = maxAttempts - attempts;
            if (remaining > 0) {
                System.out.println("Invalid password. " + remaining + " attempt(s) remaining.");
            }
        }

        System.out.println("Too many failed attempts. Exiting.");
        return null;
    }
}
