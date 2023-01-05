
Feature: Determining who plays next
  Background:
    Given no player has won the game yet
    And player 1 starts their turn with THREE-CLUBS, ACE-HEARTS, QUEEN-CLUBS, THREE-HEARTS, TWO-CLUBS
    And player 2 starts their turn with FOUR-CLUBS, TWO-HEARTS, FOUR-HEARTS, ACE-SPADES, THREE-SPADES
    And player 3 starts their turn with SEVEN-HEARTS, FIVE-CLUBS, THREE-HEARTS, FIVE-HEARTS, THREE-DIAMONDS
    And player 4 starts their turn with QUEEN-CLUBS, ACE-HEARTS, THREE-CLUBS, SEVEN-HEARTS, FOUR-CLUBS

  Scenario Outline: One player plays
      When the top card is SIX-CLUBS
      And player <current_player> is to play first
      And all players are connected
      When player <current_player> plays <card>
      Then player <next_player> should play next

      Examples:
        |row|card         |current_player|next_player|
        |41 |3-CLUBS      |1             |2          |
        |44 |QUEEN-CLUBS  |1             |3          |
        |45 |3-CLUBS      |4             |1          |
        |48 |QUEEN-CLUBS  |4             |2          |

  @Row42
  Scenario: Two players play (goes round once)
    When the top card is SIX-HEARTS
    And player 1 is to play first
    And all players are connected
    When player 1 plays ACE-HEARTS
    And the game direction is now LEFT
    And player 4 is the next to play
    And player 4 plays 7-HEARTS
    Then player 3 should play next

    @Row46
  Scenario: Two players play (goes round twice)
    When the top card is SIX-HEARTS
    And player 4 is to play first
    And all players are connected
    When player 4 plays ACE-HEARTS
    And the game direction is now LEFT
    And player 3 should play next
    And player 3 plays 7-HEARTS
    Then player 2 should play next
