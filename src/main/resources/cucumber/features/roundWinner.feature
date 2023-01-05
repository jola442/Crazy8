Feature: Round Winner Calculation
  @Row77,78
    Scenario: A player wins a round
      Given no player has won the game yet
      When the top card is THREE-CLUBS
      And player 1 is to play first
      And player 1 starts their turn with FIVE-CLUBS, ACE-SPADES
      And player 2 starts their turn with FOUR-CLUBS
      And player 3 starts their turn with EIGHT-CLUBS, JACK-HEARTS, SIX-HEARTS, KING-HEARTS, KING-SPADES
      And player 4 starts their turn with EIGHT-CLUBS, EIGHT-DIAMONDS, TWO-DIAMONDS
      And all players are connected
      And player 1 plays 5-CLUBS
      And player 2 plays 4-CLUBS
      And player 2 does not have any more cards
      And players 1,2,3 and 4 score 1, 0, 86 and 102 respectively for this round
      Then the winner of the round is player 2

