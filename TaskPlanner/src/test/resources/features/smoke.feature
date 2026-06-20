Feature: Smoke Test
  Verify that the Cucumber framework is correctly integrated and all core services can be instantiated.

  Scenario: Core services are operational
    Given the application context is initialized
    Then the registration service should be available
    And the authentication service should be available
    And the task management services should be available