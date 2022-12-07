# new feature
# Tags: optional

Feature: Ace and Queen Card functionality
  Background:
    Given the server is running

  Scenario Outline: One player plays
    And a player is connected as player <current_player>
    When player <current_player> plays <card>
    Then <next_player> should play next

    Examples:
    |row|card|current_player|next_player|
    |41 |3C  |1             |2          |
    |44 |QC  |1             |3          |
    |45 |3c  |4             |1          |
    |48 |QC  |4             |2          |


  Scenario Outline: Two players play
    And a player is connected as player <first_player>
    And a player is connected as player <second_player>
    When player <first_player> plays "1H"
    And the game direction is now "RIGHT"
    And player <second_player> plays "7H"
    Then <third_player> should play next

    Examples:
      |row|first_player|second_player|third_player|
      |42 |1           |4            |3           |
      |46 |4           |3            |2           |
