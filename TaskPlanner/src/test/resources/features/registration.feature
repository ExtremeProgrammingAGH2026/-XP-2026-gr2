Feature: User Registration
  As a user I want to create an account so that I can access the task management system.
  User Story #1

  Scenario: Successful registration returns a user with correct data
    When a user registers with name "Jan Kowalski", email "jan@example.com" and password "securePass1"
    Then the registration should succeed
    And the registered user name should be "Jan Kowalski"
    And the registered user email should be "jan@example.com"
    And the registered user should have a generated ID

  Scenario: Registration fails when email is invalid
    When a user registers with name "Jan Kowalski", email "not-an-email" and password "securePass1"
    Then the registration should fail

  Scenario: Registration fails when password is too short
    When a user registers with name "Jan Kowalski", email "jan@example.com" and password "abc"
    Then the registration should fail

  Scenario: Registration fails when name is blank
    When a user registers with name " ", email "jan@example.com" and password "securePass1"
    Then the registration should fail

  Scenario: Registration fails when email is already taken
    Given a user is already registered with name "Jan Kowalski", email "jan@example.com" and password "securePass1"
    When a user registers with name "Jan Kasztan", email "jan@example.com" and password "otherPass1"
    Then the registration should fail

  Scenario: Registered user is persisted and can be loaded from storage
    Given a user is already registered with name "Jan Kowalski", email "jan@example.com" and password "securePass1"
    When the user list is loaded from storage
    Then the loaded user list should contain 1 user

  Scenario: Multiple users can be registered with different emails
    Given a user is already registered with name "Jan Kowalski", email "jan@example.com" and password "securePass1"
    When a user registers with name "Anna Nowak", email "anna@example.com" and password "securePass2"
    Then the registration should succeed
    And the registered user email should be "anna@example.com"

  Scenario: Registration fails when email is duplicate with different case
    Given a user is already registered with name "Jan Kowalski", email "jan@example.com" and password "securePass1"
    When a user registers with name "Other User", email "JAN@EXAMPLE.COM" and password "securePass2"
    Then the registration should fail

  Scenario: Registration succeeds with password exactly 8 characters long
    When a user registers with name "Jan Kowalski", email "jan@example.com" and password "exactly8"
    Then the registration should succeed

  Scenario: Registration fails with password 7 characters long
    When a user registers with name "Jan Kowalski", email "jan@example.com" and password "seven77"
    Then the registration should fail

  Scenario: Registration fails when email has no domain
    When a user registers with name "Jan Kowalski", email "jan@" and password "securePass1"
    Then the registration should fail

  Scenario: Registration fails when name is empty string
    When a user registers with name "" , email "jan@example.com" and password "securePass1"
    Then the registration should fail