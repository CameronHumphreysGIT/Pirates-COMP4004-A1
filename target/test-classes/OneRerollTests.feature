Feature: test a single turn with one reRolls
  @Outline
  Scenario Outline:
    Given The Player has been setup as the first player
    And Initial and Reroll are setup with dice
      |<First1>|<First2>|<First3>|<First4>|<First5>|<First6>|<First7>|<First8>|
      |<Final1>|<Final2>|<Final3>|<Final4>|<Final5>|<Final6>|<Final7>|<Final8>|
    And the first and final scores for other players have been set with <First Score> and <Other Score>
    When onererollTest is run with <FortuneCard> and <reRoll>
    Then The player's last message is <First Score> <Expected Score>
    And The player socket is closed.
    Examples:
      | Row | First1    | First2    | First3    | First4    | First5    | First6    | First7    | First8    | Final1    | Final2    | Final3    | Final4    | Final5    | Final6    | Final7    | Final8    | reRoll | FortuneCard | First Score | Expected Score | Other Score |
      | 46  |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  PARROT   |  SWORD    |  SWORD    |  PARROT   |  PARROT   |  SKULL    |  PARROT   |  SKULL    |  PARROT   |  SKULL    |  SWORD    | PARROT    | "356"  | 7           | 0           | 0              | 0           |
      | 47  |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  PARROT   |  SWORD    |  SKULL    |  PARROT   |  PARROT   |  SKULL    |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  SKULL    | PARROT    | "35"   | 7           | 0           | 0              | 0           |
      | 53  |  MONKEY   |  SKULL    |  MONKEY   |  PARROT   |  SWORD    |  PARROT   |  SWORD    |  SKULL    |  MONKEY   |  SKULL    |  MONKEY   |  MONKEY   |  SWORD    |  SWORD    |  SWORD    | SKULL     | "35"   | 7           | 0           | 300            | 0           |
      | 58  |  SWORD    |  SWORD    |  PARROT   |  PARROT   |  SWORD    |  GOLD     |  SKULL    |  GOLD     |  SWORD    |  SWORD    |  GOLD     |  SWORD    |  SWORD    |  GOLD     |  SKULL    | GOLD      | "23"   | 7           | 0           | 800            | 0           |
      | 59  |  SWORD    |  SWORD    |  PARROT   |  PARROT   |  SWORD    |  GOLD     |  SKULL    |  GOLD     |  SWORD    |  SWORD    |  GOLD     |  SWORD    |  SWORD    |  GOLD     |  SKULL    | GOLD      | "23"   | 1           | 0           | 1200           | 0           |
      | 67  |  MONKEY   |  SWORD    |  MONKEY   |  MONKEY   |  SWORD    |  MONKEY   |  MONKEY   |  MONKEY   |  MONKEY   |  MONKEY   |  MONKEY   |  MONKEY   |  MONKEY   |  MONKEY   |  MONKEY   | MONKEY    | "14"   | 7           | 0           | 4600           | 0           |
      | 68  |  MONKEY   |  SWORD    |  PARROT   |  PARROT   |  SWORD    |  MONKEY   |  SKULL    |  SKULL    |  MONKEY   |  SWORD    |  DIAMOND  |  DIAMOND  |  SWORD    |  MONKEY   |  SKULL    | SKULL     | "23"   | 8           | 0           | 400            | 0           |
      | 69  |  MONKEY   |  SWORD    |  PARROT   |  DIAMOND  |  SWORD    |  MONKEY   |  SKULL    |  SKULL    |  DIAMOND  |  SWORD    |  PARROT   |  DIAMOND  |  SWORD    |  DIAMOND  |  SKULL    | SKULL     | "05"   | 7           | 0           | 500            | 0           |
      | 70  |  MONKEY   |  SWORD    |  PARROT   |  SWORD    |  SWORD    |  GOLD     |  SKULL    |  GOLD     |  MONKEY   |  PARROT   |  PARROT   |  GOLD     |  MONKEY   |  GOLD     |  SKULL    | GOLD      | "134"  | 7           | 0           | 600            | 0           |
      | 71  |  MONKEY   |  SWORD    |  PARROT   |  SWORD    |  SWORD    |  GOLD     |  SKULL    |  GOLD     |  MONKEY   |  PARROT   |  PARROT   |  GOLD     |  MONKEY   |  GOLD     |  SKULL    | GOLD      | "134"  | 8           | 0           | 500            | 0           |
      | 83  |  MONKEY   |  PARROT   |  PARROT   |  MONKEY   |  SWORD    |  GOLD     |  GOLD     |  SWORD    | MONKEY    |  PARROT   |  PARROT   |  MONKEY   |  PARROT   |  GOLD     |  GOLD     |  MONKEY   | "47"   | 9           | 0           | 1700           | 0           |
      | 100 |  MONKEY   |  SWORD    |  MONKEY   |  MONKEY   |  PARROT   |  PARROT   |  MONKEY   |  GOLD     | MONKEY    |  SWORD    |  MONKEY   |  MONKEY   |  GOLD     |  SWORD    |  MONKEY   |  GOLD     | "45"   | 4           | 0           | 1200           | 0           |
      | 110 |  SKULL    |  SKULL    |  SKULL    |  SKULL    |  MONKEY   |  SKULL    |  MONKEY   |  MONKEY   | SKULL     |  SKULL    |  SKULL    |  SKULL    |  SKULL    |  SKULL    |  GOLD     |  SKULL    | "467"  | 1           | 1500        | 1500           | 100         |
      | 111 |  SKULL    |  SWORD    |  SKULL    |  SKULL    |  SWORD    |  SWORD    |  SWORD    |  SWORD    | SKULL     |  GOLD     |  SKULL    |  SKULL    |  GOLD     |  GOLD     |  GOLD     |  GOLD     | "17654"| 10          | 600         | 600            | 100         |



