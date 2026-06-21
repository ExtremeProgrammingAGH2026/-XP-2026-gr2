package org.example;

import org.example.registration.RegistrationException;
import org.example.registration.RegistrationService;
import org.example.registration.RegistrationValidator;

import java.util.Scanner;
import java.util.function.Consumer;

public class RegistrationUI {

    private static final String CANCEL = "cancel";

    private final RegistrationService registrationService;
    private final RegistrationValidator validator;

    public RegistrationUI(RegistrationService registrationService) {
        this(registrationService, new RegistrationValidator());
    }

    public RegistrationUI(RegistrationService registrationService, RegistrationValidator validator) {
        this.registrationService = registrationService;
        this.validator = validator;
    }

    public User register(Scanner scanner) {
        System.out.println("=== Register ===");
        System.out.println("(type 'cancel' at any prompt to abort)");

        String name = promptField(scanner, "Name: ", validator::validateName);
        if (name == null) {
            System.out.println("Registration cancelled.");
            return null;
        }
        String email = promptField(scanner, "Email: ", validator::validateEmail);
        if (email == null) {
            System.out.println("Registration cancelled.");
            return null;
        }
        String password = promptField(scanner, "Password: ", validator::validatePassword);
        if (password == null) {
            System.out.println("Registration cancelled.");
            return null;
        }

        try {
            User user = registrationService.register(name, email, password);
            System.out.println("Account created. Welcome, " + user.getName() + "!");
            return user;
        } catch (RegistrationException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return null;
        }
    }

    private String promptField(Scanner scanner, String prompt, Consumer<String> fieldValidator) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase(CANCEL)) {
                return null;
            }
            try {
                fieldValidator.accept(input);
                return input;
            } catch (RegistrationException e) {
                System.out.println(e.getMessage() + ". Try again.");
            }
        }
    }
}
