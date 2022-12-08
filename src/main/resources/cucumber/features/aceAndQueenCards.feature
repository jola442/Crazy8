# new feature
# Tags: optional

Feature: Ace and Queen Card functionality
  Background: All players are connected
    Given all players are connected

  Scenario Outline: One player plays
    When player <current_player> plays <card>
    Then <next_player> should play next

    Examples:
      |row|card         |current_player|next_player|
      |41 |3-CLUBS      |1             |2          |
      |44 |QUEEN-CLUBS  |1             |3          |
      |45 |3-CLUBS      |4             |1          |
      |48 |QUEEN-CLUBS  |4             |2          |


  Scenario Outline: Two players play
    When player <first_player> plays ACE-HEARTS
    And the game direction is now RIGHT
    And player <second_player> plays 7-HEARTS
    Then <third_player> should play next

    Examples:
      |row|first_player|second_player|third_player|
      |42 |1           |4            |3           |
      |46 |4           |3            |2           |