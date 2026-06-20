Feature: User Authentication
  As a user I want to log in to the application so that I can see my tasks and duties.
  User Story #2

  Background:
    Given a user is already registered with name "Jan Kowalski", email "jan@example.com" and password "securePass1"

  Scenario: Successful login returns the correct user
    When the user logs in with email "jan@example.com" and password "securePass1"
    Then the login should succeed
    And the authenticated user name should be "Jan Kowalski"

  Scenario: Login with wrong password fails
    When the user logs in with email "jan@example.com" and password "wrongPassword"
    Then the login should fail

  Scenario: Login with unknown email fails
    When the user logs in with email "unknown@example.com" and password "securePass1"
    Then the login should fail

  Scenario: Session tracks the logged-in user
    When the user logs in with email "jan@example.com" and password "securePass1"
    And the session is started for the authenticated user
    Then the session should be active
    And the current session user should be "Jan Kowalski"

  Scenario: Logout clears the session
    When the user logs in with email "jan@example.com" and password "securePass1"
    And the session is started for the authenticated user
    And the user logs out
    Then the session should not be active