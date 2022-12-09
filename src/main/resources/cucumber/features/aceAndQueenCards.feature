# new feature
# Tags: optional

Feature: Ace and Queen Card functionality

    Scenario Outline: One player plays
      Given all players are connected and the starting card is EIGHT-CLUBS
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
    Given all players are connected and the starting card is EIGHT-HEARTS
    When player 1 plays ACE-HEARTS
    And the game direction is now RIGHT
    And player 4 plays 7-HEARTS
    Then player 3 should play next

    @Row46
  Scenario: Two players play (goes round twice)
    Given all players are connected and the starting card is EIGHT-HEARTS
    When player 4 plays ACE-HEARTS
    And the game direction is now RIGHT
    And player 3 should play next
    And player 3 plays 7-HEARTS
    Then player 2 should play next
