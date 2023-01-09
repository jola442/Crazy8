Feature:Full Gameplay
  Scenario: Full Gameplay
    Given no player has won the game yet
    When the top card is FOUR-DIAMONDS
    And player 1 is to play first
    And player 1 starts round 1 with FOUR-HEARTS, SEVEN-SPADES, FIVE-DIAMONDS, SIX-DIAMONDS, NINE-DIAMONDS
    And player 2 starts round 1 with FOUR-SPADES, SIX-SPADES, KING-CLUBS, EIGHT-HEARTS, TEN-DIAMONDS
    And player 3 starts round 1 with NINE-SPADES, SIX-CLUBS, NINE-CLUBS, JACK-DIAMONDS, THREE-HEARTS
    And player 4 starts round 1 with SEVEN-DIAMONDS, JACK-HEARTS, QUEEN-HEARTS, KING-HEARTS, FIVE-CLUBS
    And player 1 starts round 2 with SEVEN-DIAMONDS, FOUR-SPADES, SEVEN-CLUBS, FOUR-HEARTS, FIVE-DIAMONDS
    And player 2 starts round 2 with NINE-DIAMONDS, THREE-SPADES, NINE-CLUBS, THREE-HEARTS, JACK-CLUBS
    And player 3 starts round 2 with THREE-DIAMONDS, NINE-SPADES, THREE-CLUBS, NINE-HEARTS, FIVE-HEARTS
    And player 4 starts round 2 with FOUR-DIAMONDS, SEVEN-SPADES, FOUR-CLUBS, FIVE-SPADES, EIGHT-DIAMONDS
#    And player 1 starts round 1 with SEVEN-DIAMONDS, FOUR-SPADES, SEVEN-CLUBS, FOUR-HEARTS, FIVE-DIAMONDS
#    And player 2 starts round 1 with NINE-DIAMONDS, THREE-SPADES, NINE-CLUBS, THREE-HEARTS, JACK-CLUBS
#    And player 3 starts round 1 with THREE-DIAMONDS, NINE-SPADES, THREE-CLUBS, NINE-HEARTS, FIVE-HEARTS
#    And player 4 starts round 1 with FOUR-DIAMONDS, SEVEN-SPADES, FOUR-CLUBS, FIVE-SPADES, EIGHT-DIAMONDS
    And all players are connected
    And player 1 plays 4-HEARTS
    And player 2 plays 4-SPADES
    And player 3 plays 9-SPADES
    And player 4 draws TWO-CLUBS,THREE-CLUBS,FOUR-CLUBS and can't play
    And the cards at the top of the stockpile are JACK-CLUBS, TEN-CLUBS
    And player 1 plays 7-SPADES
    And player 2 plays 6-SPADES
    And player 3 plays 6-CLUBS
    And player 4 plays 2-CLUBS
    And player 1 cannot play 2 cards from their hand
    And player 1 suffers the two-card penalty, drawing 2 cards from the top of the stockpile and plays JACK-CLUBS
    And player 2 plays KING-CLUBS
    And player 3 plays 9-CLUBS
    And player 4 plays 3-CLUBS
    And player 1 chooses to draw SEVEN-CLUBS and plays it
    And player 2 declares DIAMONDS as the next suit after playing 8-HEARTS
    And player 3 plays JACK-DIAMONDS
    And player 4 plays 7-DIAMONDS
    And player 1 plays 9-DIAMONDS
    And player 2 plays 10-DIAMONDS
    And player 2 does not have any more cards
    And players 1,2,3 and 4 score 21, 0, 3 and 39 respectively for this round
#    And the winner of the round is player 2
    And player 2 should play next
#    And player 2 is to play first
#    And player 1 starts round 1 with SEVEN-DIAMONDS, FOUR-SPADES, SEVEN-CLUBS, FOUR-HEARTS, FIVE-DIAMONDS
#    And player 2 starts round 1 with NINE-DIAMONDS, THREE-SPADES, NINE-CLUBS, THREE-HEARTS, JACK-CLUBS
#    And player 3 starts round 1 with THREE-DIAMONDS, NINE-SPADES, THREE-CLUBS, NINE-HEARTS, FIVE-HEARTS
#    And player 4 starts round 1 with FOUR-DIAMONDS, SEVEN-SPADES, FOUR-CLUBS, FIVE-SPADES, EIGHT-DIAMONDS
    And player 2 plays 9-DIAMONDS
    And player 3 plays 3-DIAMONDS
    And player 4 plays 4-DIAMONDS
    And player 1 plays 4-SPADES
    And player 2 plays 3-SPADES
    And player 3 plays 9-SPADES
    And player 4 plays 7-SPADES
    And player 1 plays 7-CLUBS
    And player 2 plays 9-CLUBS
    And player 3 plays 3-CLUBS
    And player 4 plays 4-CLUBS
    And player 1 plays 4-HEARTS
    And player 2 plays 3-HEARTS
    And player 3 plays 9-HEARTS
    And player 4 chooses to draw KING-SPADES and can't play
    And player 4 chooses to draw QUEEN-SPADES and can't play
    And player 4 chooses to draw KING-HEARTS and plays it
    And player 1 draws SIX-DIAMONDS,QUEEN-DIAMONDS,JACK-DIAMONDS and can't play
    And player 2 draws SIX-SPADES,JACK-SPADES,TEN-SPADES and can't play
    And player 3 plays 5-HEARTS
    And player 3 does not have any more cards
    And players 1,2,3 and 4 score 59, 36, 3 and 114 respectively for this round
    Then the winner of the game is player 3



#  Player4 cannot play drawn card and must draw: draws QS
#  Player4 still can't play last drawn card and must draw again: draws KH
#  Player4 plays KH
#  Player1 can't play and draws 6D
#  Player1 can't play and draws QD
#  Player1 can't play and draws JD; still can't play; turn ends
#  Player2 can't play and draws 6S
#  Player2 can't play and draws JS
#  Player2 can't play and draws 10S; still can't play; turn ends
#  Player3 plays 5H and empties hand
#  Player1's hand is {5D, 6D, 7D, JD, QD} and scores 38 this round for a total of 59
#  Player2's hand is {JC, 6S, 10S, JS} and scores 36 this round for a total of 36
#  Player3's hand is empty and has a final score of 3
#  Player4's hand is {5S, 8D, KS, QS} and scores 75 this round for a total of 114, which ends the game since it is over 100
#  Player3 wins the game
