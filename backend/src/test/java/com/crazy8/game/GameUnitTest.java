package com.crazy8.game;

import org.junit.jupiter.api.Test;
import com.crazy8.game.Defs.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameUnitTest {
    Game game = new Game();
    Player player1 = new Player("Player 1");
    Player player2 = new Player("Player 2");
    Player player3 = new Player("Player 3");
    Player player4 = new Player("Player 4");

    @Test
    public void testDrawCard(){
        for(int i = 0; i < Defs.NUM_CARDS; ++i){
            game.drawCard(new Player());
        }
        assertEquals(0, game.getNumClubsCards());
        assertEquals(0, game.getNumDiamondsCards());
        assertEquals(0, game.getNumHeartsCards());
        assertEquals(0, game.getNumSpadesCards());
        assertEquals(0, game.getNumCards());
    }

    @Test
    public void testPlaceStartingCard(){
        Card startingCard = game.placeStartingCard();
        assertFalse(startingCard.getRank()==Rank.EIGHT);
    }

    @Test
    public void testStartGame(){
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        Player player3 = new Player("Player 3");
        Player player4 = new Player("Player 4");
        game.getPlayers().addAll(Arrays.asList(player1, player2, player3, player4));
        game.startRound();
        assertFalse(game.getTopCard()==null);
        assertFalse(player1.getHand().contains(null));
        assertFalse(player2.getHand().contains(null));
        assertFalse(player3.getHand().contains(null));
        assertFalse(player4.getHand().contains(null));
        assertEquals(5, player1.getHand().size());
        assertEquals(5, player2.getHand().size());
        assertEquals(5, player3.getHand().size());
        assertEquals(5, player4.getHand().size());
        assertEquals(31, game.getNumCards());
    }

    @Test
    public void testPlayCard(){
        //Same rank, different suit
        game.setTopCard(new Card(Rank.FOUR,Suit.HEARTS));
        player1.getHand().add(new Card(Rank.FOUR, Suit.CLUBS));
        Card cardPlayed = game.playCard(player1, new Card(Rank.FOUR, Suit.CLUBS));
        assertNotNull(cardPlayed);

        //Same suit, different rank
        game.setTopCard(new Card(Rank.FOUR,Suit.HEARTS));
        player2.getHand().add(new Card(Rank.THREE, Suit.HEARTS));
        cardPlayed = game.playCard(player2, new Card(Rank.THREE, Suit.HEARTS));
        assertNotNull(cardPlayed);

        //An 8, different suit, different rank
        game.setTopCard(new Card(Rank.FOUR,Suit.HEARTS));
        player3.getHand().add(new Card(Rank.EIGHT, Suit.CLUBS));
        cardPlayed = game.playCard(player3, new Card(Rank.EIGHT, Suit.CLUBS));
        assertNotNull(cardPlayed);

        //Not an 8, different rank, different suit
        game.setTopCard(new Card(Rank.FOUR,Suit.HEARTS));
        player1.getHand().add(new Card(Rank.TWO, Suit.CLUBS));
        cardPlayed = game.playCard(player1, new Card(Rank.TWO, Suit.CLUBS));
        assertNull(cardPlayed);

        //Same rank but the player does not have the card
        game.setTopCard(new Card(Rank.FOUR,Suit.HEARTS));
        player2.getHand().add(new Card(Rank.TWO, Suit.CLUBS));
        cardPlayed = game.playCard(player2, new Card(Rank.FOUR, Suit.CLUBS));
        assertNull(cardPlayed);

    }
}