package org.example;

import java.util.Scanner;

public class StartScreenUI {

    private final LoginUI loginUI;
    private final RegistrationUI registrationUI;

    public StartScreenUI(LoginUI loginUI, RegistrationUI registrationUI) {
        this.loginUI = loginUI;
        this.registrationUI = registrationUI;
    }

    public User run(Scanner scanner) {
        System.out.println("=== Task Planner ===");
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    User logged = loginUI.login(scanner);
                    if (logged != null) return logged;
                    break;
                case "2":
                    User registered = registrationUI.register(scanner);
                    if (registered != null) return registered;
                    break;
                case "3":
                    System.out.println("Goodbye.");
                    return null;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
