Feature: test a single turn with one reRolls
  @Outline
  Scenario Outline:
    Given The Player has been setup as the first player
    And Initial and Reroll are setup with dice
      |<First1>|<First2>|<First3>|<First4>|<First5>|<First6>|<First7>|<First8>|
      |<Final1>|<Final2>|<Final3>|<Final4>|<Final5>|<Final6>|<Final7>|<Final8>|
    When NorerollTest is run with <FortuneCard>
    Then The player's last message is <Expected Score>
    And The player socket is closed.
    Examples:
      | Row | First1    | First2    | First3    | First4    | First5    | First6    | First7    | First8    | Final1    | Final2    | Final3    | Final4    | Final5    | Final6    | Final7    | Final8    | FortuneCard | Expected Score |