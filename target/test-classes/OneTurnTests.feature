Feature: 45,52,54,55,56,57,62,63,64,65,66,72,82,84,97,98,99,103,106,107,117,120,123 test a single turn with no reRolls
    @Outline
      Scenario Outline:
        Given The Player has been setup as the first player
        And Setup is made with dice
        |<Dice1>|<Dice2>|<Dice3>|<Dice4>|<Dice5>|<Dice6>|<Dice7>|<Dice8>|
        When NorerollTest is run with <FortuneCard>
        Then The player's last message is <Expected Score>
        And The player socket is closed.
        Examples:
          | Row | Dice1    | Dice2    | Dice3    | Dice4    | Dice5    | Dice6    | Dice7    | Dice8    | FortuneCard | Expected Score |
          | 45  |  SKULL   | SWORD    | SKULL    | SWORD    | SWORD    | SWORD    | SKULL    | SWORD    | 7           | 0              |
          | 52  |  DIAMOND | PARROT   | PARROT   | MONKEY   | DIAMOND  | GOLD     | GOLD     | MONKEY   | 1           | 800            |
          | 54  |  SWORD   | SWORD    | MONKEY   | SWORD    | SKULL    | MONKEY   | SKULL    | MONKEY   | 7           | 300            |
          | 55  |  DIAMOND | DIAMOND  | SWORD    | DIAMOND  | PARROT   | SKULL    | SKULL    | MONKEY   | 7           | 500            |
          | 56  |  GOLD    | GOLD     | SKULL    | SWORD    | GOLD     | SWORD    | SKULL    | GOLD     | 8           | 700            |
          | 57  |  SKULL   | PARROT   | PARROT   | SWORD    | PARROT   | SWORD    | PARROT   | SWORD    | 7           | 400            |
          | 62  |  MONKEY  | MONKEY   | SKULL    | MONKEY   | MONKEY   | MONKEY   | SKULL    | MONKEY   | 7           | 1100           |
          | 63  |  PARROT  | PARROT   | PARROT   | PARROT   | PARROT   | PARROT   | SKULL    | PARROT   | 7           | 2100           |
          | 64  |  GOLD    | GOLD     | GOLD     | GOLD     | GOLD     | GOLD     | GOLD     | GOLD     | 7           | 5400           |
          | 65  |  GOLD    | GOLD     | GOLD     | GOLD     | GOLD     | GOLD     | GOLD     | GOLD     | 8           | 5400           |
          | 66  |  SWORD   | SWORD    | SWORD    | SWORD    | SWORD    | SWORD    | SWORD    | SWORD    | 1           | 9000           |
          | 72  |  MONKEY  | GOLD     | SKULL    | MONKEY   | GOLD     | MONKEY   | MONKEY   | SKULL    | 7           | 600            |
          | 82  |  MONKEY  | PARROT   | PARROT   | MONKEY   | PARROT   | GOLD     | SKULL    | MONKEY   | 9           | 1100           |
          | 84  |  MONKEY  | PARROT   | SKULL    | MONKEY   | PARROT   | SKULL    | MONKEY   | SKULL    | 9           | 0              |
          | 97  |  MONKEY  | SWORD    | SWORD    | MONKEY   | SWORD    | DIAMOND  | MONKEY   | PARROT   | 7           | 400            |
          | 98  |  MONKEY  | SWORD    | SWORD    | MONKEY   | GOLD     | SWORD    | MONKEY   | GOLD     | 1           | 1800           |
          | 99  |  MONKEY  | SWORD    | SWORD    | MONKEY   | DIAMOND  | SWORD    | MONKEY   | SWORD    | 7           | 1000           |
          | 103 |  MONKEY  | GOLD     | MONKEY   | DIAMOND  | PARROT   | DIAMOND  | DIAMOND  | GOLD     | 9           | 1200           |
          | 106 |  SKULL   | SWORD    | SWORD    | SWORD    | SWORD    | SWORD    | SWORD    | SWORD    | 10          | 0              |
          | 107 |  SKULL   | SWORD    | SWORD    | SKULL    | SWORD    | SWORD    | SWORD    | SWORD    | 3           | 0              |
          | 117 |  PARROT  | SWORD    | MONKEY   | MONKEY   | MONKEY   | SWORD    | PARROT   | GOLD     | 4           | 500            |
          | 120 |  SWORD   | SWORD    | MONKEY   | MONKEY   | SWORD    | SKULL    | SWORD    | MONKEY   | 5           | 800            |
          | 123 |  MONKEY  | SWORD    | MONKEY   | MONKEY   | SWORD    | SWORD    | SWORD    | SKULL    | 6           | 1300           |