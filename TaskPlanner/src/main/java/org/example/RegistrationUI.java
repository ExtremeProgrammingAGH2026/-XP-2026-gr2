package org.example;

import org.example.registration.RegistrationException;
import org.example.registration.RegistrationService;

import java.util.Scanner;

public class RegistrationUI {

    private final RegistrationService registrationService;

    public RegistrationUI(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public User register(Scanner scanner) {
        System.out.println("=== Register ===");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        try {
            User user = registrationService.register(name, email, password);
            System.out.println("Account created. Welcome, " + user.getName() + "!");
            return user;
        } catch (RegistrationException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return null;
        }
    }
}
