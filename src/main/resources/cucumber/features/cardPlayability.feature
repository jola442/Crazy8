Feature: Card Playability
  Background:
    Given card playability functionality is being tested

  @Row51,52
  Scenario Outline: A player plays a valid card
    When the starting card is KING-CLUBS
    And all players are connected
    When player 1 plays <card>
    Then the top card should be <card>

    Examples:
      |row|card         |
      |51 |KING-HEARTS  |
      |52 |QUEEN-CLUBS  |


  @Row53
  Scenario: A player is prompted for a new suit after playing an 8
    When the starting card is KING-CLUBS
    And all players are connected
    When player 1 plays EIGHT-HEARTS
    Then the game should prompt the player for a new suit

  @Row54
  Scenario: A player is shown a message after playing an invalid card
    When the starting card is KING-CLUBS
    And all players are connected
    When player 1 attempts to play 5-SPADES
    Then the game should send a message saying the card is invalid


