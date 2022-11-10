Feature: test a single turn with one reRolls
  @Outline
  Scenario Outline:
    Given The Player has been setup as the first player
    And Initial and Reroll are setup with dice
      |<First1>|<First2>|<First3>|<First4>|<First5>|<First6>|<First7>|<First8>|
      |<Final1>|<Final2>|<Final3>|<Final4>|<Final5>|<Final6>|<Final7>|<Final8>|
    When onererollTest is run with <FortuneCard> and <reRoll>
    Then The player's last message is <First Score> <Expected Score>
    And The player socket is closed.
    Examples: "PARROT", "SKULL", "PARROT", "SWORD", "PARROT", "SWORD", "SKULL", "PARROT"
      | Row | First1    | First2    | First3    | First4    | First5    | First6    | First7    | First8    | Final1    | Final2    | Final3    | Final4    | Final5    | Final6    | Final7    | Final8    | reRoll | FortuneCard | First Score | Expected Score |
      | 46  |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  PARROT   |  SWORD    |  SWORD    |  PARROT   |  PARROT   |  SKULL    |  PARROT   |  SKULL    |  PARROT   |  SKULL    |  SWORD    | PARROT    | "356"  | 7           | 0           | 0              |
      | 47  |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  PARROT   |  SWORD    |  SKULL    |  PARROT   |  PARROT   |  SKULL    |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  SKULL    | PARROT    | "35"   | 7           | 0           | 0              |
