#Feature: Card Playability
#  Background:
#    Given no player has won the game yet
#    And player 1 starts their turn with KING-HEARTS, SEVEN-CLUBS, EIGHT-HEARTS, FIVE-SPADES, QUEEN-CLUBS
#    And player 2 starts their turn with FOUR-CLUBS, TWO-HEARTS, FOUR-HEARTS, ACE-SPADES, THREE-SPADES
#    And player 3 starts their turn with SEVEN-HEARTS, FIVE-CLUBS, THREE-HEARTS, FIVE-HEARTS, THREE-DIAMONDS
#    And player 4 starts their turn with QUEEN-CLUBS, ACE-HEARTS, THREE-CLUBS, SEVEN-HEARTS, FOUR-CLUBS
#
#  @Row51,52
#  Scenario Outline: A player plays a valid card
#    When the top card is KING-CLUBS
#    And player 1 is to play first
#    And all players are connected
#    When player 1 plays <card>
#    Then the top card should be <card>
#
#    Examples:
#      |row|card         |
#      |51 |KING-HEARTS  |
#      |52 |QUEEN-CLUBS  |
#
#
#  @Row53
#  Scenario: A player is prompted for a new suit after playing an 8
#    When the top card is KING-CLUBS
#    And player 1 is to play first
#    And all players are connected
#    When player 1 plays 8-HEARTS
#    Then the game should prompt the player 1 for a new suit
#
#  @Row54
#  Scenario: A player is shown a message after playing an invalid card
#    When the top card is KING-CLUBS
#    And player 1 is to play first
#    And all players are connected
#    When player 1 attempts to play 5-SPADES
#    Then the game should send player 1 a message saying the card is invalid
#
#
