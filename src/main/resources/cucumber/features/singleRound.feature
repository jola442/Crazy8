Feature: Single round
  @Row77,78
    Scenario: Single round played
      Given the players wish to play a single round
      And all players are connected
      And the players play their cards
      Then the game is over with players 1,2,3,4 scoring 1, 0, 86 and 102 respectively