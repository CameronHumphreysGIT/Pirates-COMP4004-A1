Feature: test a single turn with no reRolls
    @Outline
      Scenario Outline:
        Given The Player has been setup as the first player
        When NoReRollTest is run with <InitialRoll> and <FortuneCard>
        Then The player's last message is <Expected Score>
        And The player socket is closed.
        Examples:
          | Row | InitialRoll                                                                    | FortuneCard | Expected Score |
          | 45  | "SKULL", "SWORD", "SKULL", "SWORD", "SWORD", "SWORD", "SKULL", "SWORD"         | 7           | 0              |
          | 52  | "DIAMOND", "PARROT", "PARROT", "MONKEY", "DIAMOND", "GOLD", "GOLD", "MONKEY"   | 1           | 800            |
          | 54  | "SWORD", "SWORD", "MONKEY", "SWORD", "SKULL", "MONKEY", "SKULL", "MONKEY"      | 7           | 300            |
          | 55  | "DIAMOND", "DIAMOND", "SWORD", "DIAMOND", "PARROT", "SKULL", "SKULL", "MONKEY" | 7           | 500            |
          | 56  | "GOLD", "GOLD", "SKULL", "SWORD", "GOLD", "SWORD", "SKULL", "GOLD"             | 8           | 700            |
          | 57  | "SKULL", "PARROT", "PARROT", "SWORD", "PARROT", "SWORD", "PARROT", "SWORD"     | 7           | 400            |
          | 62  | "MONKEY", "MONKEY", "SKULL", "MONKEY", "MONKEY", "MONKEY", "SKULL", "MONKEY"   | 7           | 1100           |
          | 63  | "PARROT", "PARROT", "PARROT", "PARROT", "PARROT", "PARROT", "SKULL", "PARROT"  | 7           | 2100           |
          | 64  | "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD"                 | 7           | 5400           |
          | 65  | "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD"                 | 8           | 5400           |
          | 66  | "SWORD", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD"         | 1           | 9000           |
          | 72  | "MONKEY", "GOLD", "SKULL", "MONKEY", "GOLD", "MONKEY", "MONKEY", "SKULL"       | 7           | 600            |
          | 82  | "MONKEY", "PARROT", "PARROT", "MONKEY", "PARROT", "GOLD", "SKULL", "MONKEY"    | 9           | 1100           |
          | 84  | "MONKEY", "PARROT", "SKULL", "MONKEY", "PARROT", "SKULL", "MONKEY", "SKULL"    | 9           | 0              |
