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
