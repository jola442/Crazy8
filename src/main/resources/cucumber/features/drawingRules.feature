Feature: Drawing Rules

  Background:
    Given no player has won the game yet
    And player 1 starts their turn with THREE-HEARTS

  @Row58,59,60
  Scenario Outline: A player only has a 3-HEARTS in their hand, must draw a card and play it
    When the top card is SEVEN-CLUBS
    And player 1 is to play first
    And all players are connected
#    And player 1 attempts to play 3-HEARTS
    And player 1 draws <drawn_cards> and plays <card>
    Then the top card should be <card>

    Examples:
      |row|card         |drawn_cards                          |
      |58 |6-CLUBS      |SIX-CLUBS                            |
      |59 |5-CLUBS      |SIX-DIAMONDS,FIVE-CLUBS              |
      |60 |7-HEARTS     |SIX-DIAMONDS,FIVE-SPADES,SEVEN-HEARTS|

    @Row61
  Scenario: A player only has a 3-HEARTS in their hand and cannot play after drawing 3 cards
    When the top card is SEVEN-CLUBS
    And player 1 is to play first
    And all players are connected
#    And player 1 attempts to play 3-HEARTS
    And player 1 draws SIX-DIAMONDS,FIVE-SPADES,FOUR-HEARTS and can't play
    Then the top card should be 7-CLUBS


    @Row62
    Scenario: A player only has a 3-HEARTS in their hand, draw an 8 and play it
      When the top card is SEVEN-CLUBS
      And player 1 is to play first
      And all players are connected
#      And player 1 attempts to play 3-HEARTS
      And player 1 draws SIX-DIAMONDS,EIGHT-HEARTS and plays 8-HEARTS
      Then the game should prompt the player 1 for a new suit

    @Row63
    Scenario: A player only has a 3-HEARTS and KING-SPADES and chooses to draw
      When the top card is SEVEN-CLUBS
      And player 1 is to play first
      And all players are connected
      And player 1 chooses to draw EIGHT-CLUBS and plays it
      Then the game should prompt the player 1 for a new suit
