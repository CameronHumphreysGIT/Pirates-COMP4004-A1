Feature: test a single turn with one reRolls
  @Outline
  Scenario Outline:
    Given The Player has been setup as the first player
    And Initial, Second and Reroll are setup with dice
      |<First1>|<First2>|<First3>|<First4>|<First5>|<First6>|<First7>|<First8>|
      |<Second1>|<Second2>|<Second3>|<Second4>|<Second5>|<Second6>|<Second7>|<Second8>|
      |<Final1>|<Final2>|<Final3>|<Final4>|<Final5>|<Final6>|<Final7>|<Final8>|
    And the first and final scores for other players have been set with <First Score> and <Other Score>
    When tworerollTest is run with <FortuneCard> and <reRoll> and <reRollTwo>
    Then The player's last message is <First Score> <Expected Score>
    And The player socket is closed.
    Examples:
      | Row | First1    | First2    | First3    | First4    | First5    | First6    | First7    | First8    | Second1   | Second2   | Second3   | Second4   | Second5   | Second6   | Second7   | Second8   | Final1    | Final2    | Final3    | Final4    | Final5    | Final6    | Final7    | Final8    | reRoll | reRollTwo | FortuneCard | First Score | Expected Score | Other Score |
      | 48  |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  PARROT   |  SWORD    |  SWORD    |  PARROT   | PARROT    |  SKULL    |  PARROT   |  SKULL    |  PARROT   |  MONKEY   |  MONKEY   |  PARROT   | PARROT    |  SKULL    |  PARROT   |  SKULL    |  PARROT   |  SKULL    |  MONKEY   |  PARROT   | "356"  | "56"      | 7           | 0           | 0              | 0           |
      | 50  |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  GOLD     |  SWORD    |  SWORD    |  GOLD     | GOLD      |  SKULL    |  GOLD     |  SWORD    |  GOLD     |  SWORD    |  SWORD    |  GOLD     | GOLD      |  SKULL    |  GOLD     |  GOLD     |  GOLD     |  GOLD     |  GOLD     |  GOLD     | "02"   | "356"     | 7           | 0           | 4800           | 0           |


