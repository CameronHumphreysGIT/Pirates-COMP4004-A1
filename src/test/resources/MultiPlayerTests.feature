Feature: 130 three player game end scenario.
  Scenario:
    Given The Server has been created
    And Each player has been created
    And Each player subsequently joins the game
    When Each Player has a fortune card and rolls
      | 1        |  1          | SWORD,SWORD,SWORD,SWORD,SKULL,SWORD,SWORD,SWORD |
      | 2        |  3          | SKULL,SWORD,SWORD,SWORD,SWORD,SWORD,SWORD,SWORD |
      | 3        |  7          | MONKEY,MONKEY,SKULL,MONKEY,SKULL,MONKEY,MONKEY,SKULL |
    Then PlayerOne's Score is 4000
    And PlayerTwo's Score is 2000
    And PlayerThree's Score is 0
    And Each player's last Message is "Winner1"