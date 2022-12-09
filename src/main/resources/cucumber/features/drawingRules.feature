Feature: Drawing Rules
#  @Row58,59,60
#  Scenario Outline: A player only has a 3-HEARTS in their hand, must draw a card and play it
#    Given draw card functionality is being tested
#    When the starting card is SEVEN-CLUBS
#    And all players are connected
#    And player 1 attempts to play 3-HEARTS
#    Then player 1 draws <drawn_cards> and plays <card>
#
#    Examples:
#      |row|card         |drawn_cards                          |
#      |58 |6-CLUBS      |SIX-CLUBS                            |
#      |59 |5-CLUBS      |SIX-DIAMONDS,FIVE-CLUBS              |
#      |60 |7-HEARTS     |SIX-DIAMONDS,FIVE-SPADES,SEVEN-HEARTS|

    @Row61
  Scenario: A player only has a 3-HEARTS in their hand and cannot play after drawing 3 cards
    Given draw card functionality is being tested
    When the starting card is SEVEN-CLUBS
    And all players are connected
    And player 1 attempts to play 3-HEARTS
    Then player 1 draws SIX-DIAMONDS,FIVE-SPADES,FOUR-HEARTS and can't play


    @Row62
    Scenario: A player only has a 3-HEARTS in their hand, draw an 8 and play it
      Given draw card functionality is being tested
      When the starting card is SEVEN-CLUBS
      And all players are connected
      And player 1 attempts to play 3-HEARTS
      And player 1 draws SIX-DIAMONDS,EIGHT-HEARTS and plays 8-HEARTS
      Then the game should prompt the player 1 for a new suit

    @Row63
    Scenario: A player only has a 3-HEARTS and KING-SPADES and chooses to draw
      Given optional draw card functionality is being tested
      When the starting card is 7-CLUBS
      And all players are connected
      And player 1 chooses to draw SIX-DIAMONDS, EIGHT-HEARTS and plays it
      Then the game should prompt the player 1 for a new suit
