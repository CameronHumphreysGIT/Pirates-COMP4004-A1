Feature: 130,134,142 three player game end scenario.
  #line 130
  Scenario:
    Given The Server has been created
    And Each player has been created
    And Each player subsequently joins the game
    When Each Player has a fortune card and rolls
      | 1        |  1          | SWORD,SWORD,SWORD,SWORD,SKULL,SWORD,SWORD,SWORD |
      | 2        |  3          | SKULL,SWORD,SWORD,SWORD,SWORD,SWORD,SWORD,SWORD |
      | 3        |  7          | MONKEY,MONKEY,SKULL,MONKEY,SKULL,MONKEY,MONKEY,SKULL |
    And The game is ended
    Then PlayerOne's Score is 4000
    And PlayerTwo's Score is 2000
    And PlayerThree's Score is 0
    And Each player's last Message is "Winner1"
  #line 134
  Scenario:
    Given The Server has been created
    And Each player has been created
    And Each player subsequently joins the game
    When Each Player has a fortune card and rolls
      | 1        |  1          | SWORD,SWORD,SWORD,SWORD,SKULL,SWORD,SWORD,SWORD |
      | 2        |  7          | MONKEY,MONKEY,SKULL,MONKEY,SKULL,MONKEY,MONKEY,SKULL |
      | 3        |  1          | SKULL,PARROT,SKULL,SKULL,PARROT,SKULL,SKULL,SKULL |
    Then PlayerOne's Score is 2800
    And PlayerTwo's Score is 0
    When Each Player has a fortune card and rolls
      | 1        |  7          | PARROT,PARROT,MONKEY,MONKEY,PARROT,MONKEY,PARROT,MONKEY |
      | 2        |  1          | MONKEY,MONKEY,SKULL,MONKEY,SKULL,MONKEY,MONKEY,SKULL |
      | 3        |  3          | MONKEY,MONKEY,SKULL,MONKEY,SKULL,MONKEY,MONKEY,MONKEY|
    And The game is ended
    Then PlayerOne's Score is 3800
    And PlayerTwo's Score is 0
    And PlayerThree's Score is 0
    And Each player's last Message is "Winner1"
  #line 142
  Scenario:
    Given The Server has been created
    And Each player has been created
    And Each player subsequently joins the game
    When Each Player has a fortune card and rolls
      | 1        |  1          | MONKEY,MONKEY,SKULL,MONKEY,SKULL,MONKEY,MONKEY,SKULL |
      | 2        |  1          | SWORD,SKULL,SWORD,SWORD,SWORD,SWORD,SWORD,SWORD |
      | 3        |  10         | SWORD,SWORD,SWORD,SWORD,SWORD,SWORD,SKULL,SWORD |
    And The first player rolls again
      | 1        |  1          | SWORD,SWORD,SWORD,SWORD,SWORD,SWORD,SWORD,SWORD |
    And The game is ended
    Then PlayerOne's Score is 9000
    And PlayerTwo's Score is 4000
    And PlayerThree's Score is 0
    And Each player's last Message is "Winner1"