Feature: all scenarios for every acceptance test
    #following for lines: 45,52,54,55,56,57,62,63,64,65,66,72,82,84,97,98,99,103,106,107,117,120,123 no reroll tests
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
    #following for lines: 46, 47, 53, 58,59,67,69,70,71,83,100,110,111,115,118,121 single reroll tests
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
        | 115 |  SKULL    |  SWORD    |  SKULL    |  PARROT   |  PARROT   |  SWORD    |  PARROT   |  PARROT   | SKULL     |  SWORD    |  SKULL    |  SKULL    |  SKULL    |  SWORD    |  SKULL    |  SKULL    | "3467" | 5           | 500         | 0              | 0           |
        | 118 |  PARROT   |  SWORD    |  MONKEY   |  MONKEY   |  MONKEY   |  SKULL    |  PARROT   |  MONKEY   | SWORD     |  SWORD    |  MONKEY   |  MONKEY   |  MONKEY   |  SKULL    |  SKULL    |  MONKEY   | "06"   | 4           | 0           | 500            | 0           |
        | 121 |  MONKEY   |  SWORD    |  MONKEY   |  MONKEY   |  SKULL    |  SKULL    |  SWORD    |  MONKEY   | SKULL     |  SWORD    |  SWORD    |  SKULL    |  SKULL    |  SKULL    |  SWORD    |  SWORD    | "0237" | 5           | 500         | 0              | 0           |
    #following for lines: 48, 50,60,77,78,79,108,124 two reroll tests
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
        | 60  |  MONKEY   |  SWORD    |  PARROT   |  PARROT   |  SWORD    |  MONKEY   |  SKULL    |  SWORD    | SKULL     |  SWORD    |  PARROT   |  PARROT   |  SWORD    |  SWORD    |  SKULL    |  SWORD    | SKULL     |  SWORD    |  SWORD    |  MONKEY   |  SWORD    |  SWORD    |  SKULL    |  SWORD    | "05"   | "23"      | 7           | 0           | 600            | 0           |
        | 77  |  MONKEY   |  SWORD    |  PARROT   |  DIAMOND  |  GOLD     |  DIAMOND  |  PARROT   |  PARROT   | MONKEY    |  SWORD    |  SKULL    |  DIAMOND  |  GOLD     |  DIAMOND  |  MONKEY   |  MONKEY   | MONKEY    |  SWORD    |  MONKEY   |  DIAMOND  |  GOLD     |  DIAMOND  |  MONKEY   |  MONKEY   | "267"  | "2"       | 2           | 0           | 500            | 0           |
        | 78  |  SKULL    |  SWORD    |  PARROT   |  SKULL    |  PARROT   |  SWORD    |  SKULL    |  PARROT   | SKULL     |  SWORD    |  PARROT   |  PARROT   |  PARROT   |  SWORD    |  SKULL    |  PARROT   | SKULL     |  PARROT   |  PARROT   |  PARROT   |  PARROT   |  PARROT   |  SKULL    |  PARROT   | "3"    | "15"      | 2           | 0           | 1000           | 0           |
        | 79  |  PARROT   |  MONKEY   |  PARROT   |  MONKEY   |  PARROT   |  MONKEY   |  SKULL    |  PARROT   | PARROT    |  PARROT   |  PARROT   |  SKULL    |  PARROT   |  PARROT   |  SKULL    |  PARROT   | PARROT    |  PARROT   |  PARROT   |  PARROT   |  PARROT   |  PARROT   |  SKULL    |  PARROT   | "135"  | "3"       | 2           | 0           | 2000           | 0           |
        | 108 |  SKULL    |  PARROT   |  PARROT   |  SKULL    |  MONKEY   |  PARROT   |  MONKEY   |  MONKEY   | SKULL     |  SKULL    |  SWORD    |  SKULL    |  MONKEY   |  SKULL    |  MONKEY   |  MONKEY   | SKULL     |  SKULL    |  SKULL    |  SKULL    |  SKULL    |  SKULL    |  SWORD    |  SKULL    | "125"  | "2467"    | 10          | 1000        | 1000           | 100         |
        | 124 |  SKULL    |  PARROT   |  PARROT   |  DIAMOND  |  SWORD    |  MONKEY   |  MONKEY   |  MONKEY   | SKULL     |  SWORD    |  SWORD    |  DIAMOND  |  SWORD    |  MONKEY   |  MONKEY   |  MONKEY   | SKULL     |  SWORD    |  SWORD    |  DIAMOND  |  SWORD    |  PARROT   |  SWORD    |  PARROT   | "12"   | "567"     | 6           | 0           | 1300           | 0           |
    #following for lines 130,134,142,147 three player game end scenarios
    #NOTE the following tests have timeouts and use some networking, they take a few minutes to run through.
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
    #line 147
    Scenario:
      Given The Server has been created
      And Each player has been created
      And Each player subsequently joins the game
      When The first player rolls again
        | 1        |  7          | SWORD,SKULL,SWORD,SWORD,SKULL,SWORD,SWORD,SWORD |
      Then PlayerOne's Score is 1100
      And The second player rolls and rerolls
        | 2        |  2          | SKULL,SKULL,SKULL,GOLD,SKULL,SKULL,SKULL,SKULL | SKULL,PARROT,SKULL,GOLD,SKULL,SKULL,SKULL,SKULL | SKULL,SKULL,SKULL,SKULL,SKULL,SKULL,SKULL,SKULL | 1 | 13 |
      And The game is ended
      Then PlayerOne's Score is 300
      And PlayerTwo's Score is 0
      And PlayerThree's Score is 0
      #expected is round 0 since spec doesn't say to complete a round ie: only player 1 and 2 do a turn
      And Each player's last Message is "Round: 0 Player1 Score: 300 Player2 Score: 0 Player3 Score: 0"


