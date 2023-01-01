Feature: Two Cards functionality
  Background:
    Given no player has won the game yet
    And player 3 starts their turn with SEVEN-DIAMONDS
    And player 4 starts their turn with FOUR-DIAMONDS

  @Row67
  Scenario: Player 1 plays a 2 and player 2 draws 2 cards and plays one
    And player 1 starts their turn with TWO-CLUBS, THREE-SPADES
    And player 2 starts their turn with FOUR-HEARTS
    And the cards at the top of the stockpile are SIX-CLUBS, NINE-DIAMONDS
    And player 1 is to play first
    And all players are connected
    When player 1 plays 2-CLUBS
    And player 2 cannot play 2 cards from their hand
    And player 2 suffers the two-card penalty, drawing 2 cards from the top of the stockpile and playing SIX-CLUBS
    Then the top card should be 6-CLUBS

  @Row68
  Scenario: Player 1 plays a 2 and player 2 draws 2 cards, can't play either, draws 2 more and plays one
    And player 1 starts their turn with TWO-CLUBS, THREE-SPADES
    And player 2 starts their turn with FOUR-HEARTS
    And player 1 is to play first
    And the cards at the top of the stockpile are SIX-SPADES, NINE-DIAMONDS, NINE-HEARTS, SIX-CLUBS
    And all players are connected
    When player 1 plays 2-CLUBS
    And player 2 cannot play 2 cards from their hand
    And player 2 suffers the two-card penalty, drawing 2 cards from the top of the stockpile and can't play either
    But player 2's turn is not over so they draw NINE-HEARTS, SIX-CLUBS and play 6-CLUBS
    Then the top card should be 6-CLUBS

  @Row69
  Scenario: Player 1 plays a 2 and player 2 draws 2 cards, can't play either, draws 3 more and still can't play
    And player 1 starts their turn with TWO-CLUBS, THREE-SPADES
    And player 2 starts their turn with FOUR-HEARTS
    And player 1 is to play first
    And the cards at the top of the stockpile are SIX-CLUBS, NINE-DIAMONDS, NINE-HEARTS, SEVEN-SPADES, FIVE-HEARTS
    And all players are connected
    When player 1 plays 2-CLUBS
    And player 2 cannot play 2 cards from their hand
    And player 2 suffers the two-card penalty, drawing 2 cards from the top of the stockpile and can't play either
    But player 2's turn is not over so they draw NINE-HEARTS, SEVEN-SPADES, FIVE-HEARTS and still can't play
    Then the top card should be 2-CLUBS


  Scenario: Player 1 plays a 2, player 2 plays a 2 and player 3 draws 4 cards and plays one
    And player 1 starts their turn with TWO-CLUBS, THREE-SPADES
    And player 2 starts their turn with FOUR-HEARTS
    And player 1 is to play first
    And the cards at the top of the stockpile are THREE-HEARTS, NINE-DIAMONDS, FIVE-SPADES, SIX-DIAMONDS
    And all players are connected
    When player 1 plays 2-CLUBS
    And player 2 plays 2-SPADES
    And player 3 suffers the two-card penalty, drawing 4 cards from the top of the stockpile and playing FIVE-SPADES
    Then the top card should be 5-SPADES

  @Row72
  Scenario: Player 1 plays a 2 and player 2 plays 2 cards
    And player 1 starts their turn with TWO-CLUBS, THREE-SPADES
    And player 2 starts their turn with FOUR-CLUBS, SIX-CLUBS, NINE-DIAMONDS
    And player 1 is to play first
    And all players are connected
    When player 1 plays 2-CLUBS
    And player 2 avoids the two-card penalty by playing 4-CLUBS, 6-CLUBS
    Then the top card should be 6-CLUBS

  @Row73
  Scenario: Player 1 plays a 2 and player 2 plays 2 cards and the round ends
    And player 1 starts their turn with TWO-CLUBS, THREE-SPADES
    And player 2 starts their turn with FOUR-CLUBS, SIX-CLUBS
    And player 1 is to play first
    And all players are connected
    When player 1 plays 2-CLUBS
    And player 2 avoids the two-card penalty by playing 4-CLUBS, 6-CLUBS
    And player 2 does not have any more cards
    And players 1,2,3 and 4 score 3, 0, 0 and 0 respectively for this round
    Then the winner of the round is player 2







