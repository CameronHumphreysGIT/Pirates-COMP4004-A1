Feature: test a single turn with no reRolls
    @Outline
      Scenario Outline:
        Given The Player has been setup as the first player
        When NoReRollTest is run with <InitialRoll> and <FortuneCard>
        Then The player's last message is <Expected Score>
        Examples:
          | Row | InitialRoll                                                              | FortuneCard | Expected Score |
          | 45  | "SKULL", "SWORD", "SKULL", "SWORD", "SWORD", "SWORD", "SKULL", "SWORD"   | 7           | 0              |

