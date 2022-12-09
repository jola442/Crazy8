Feature: Ace and Queen Card functionality
  Background:
    Given all players are connected and the starting card is KING-CLUBS

  @Row51,52
  Scenario Outline: A player plays a valid card
    When player 1 plays <card>
    Then the top card should be <card>

    Examples:
      |row|card         |
      |51 |KING-HEARTS  |
      |52 |QUEEN-CLUBS  |


  @Row53
  Scenario: Two players play (goes round once)
    When player 1 plays EIGHT-HEARTS
    Then the game should prompt the player for a new suit

  @Row54
  Scenario: Two players play (goes round twice)
    When player 1 plays 5-SPADES
    Then the game should send a message saying the card is invalid


