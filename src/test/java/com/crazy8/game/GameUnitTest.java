package com.crazy8.game;

import org.junit.jupiter.api.Test;
import com.crazy8.game.Defs.*;

import java.util.ArrayList;
import java.util.Arrays;

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

//    @Test
//    public void testStartGame(){
//        Player player1 = new Player("Player 1");
//        Player player2 = new Player("Player 2");
//        Player player3 = new Player("Player 3");
//        Player player4 = new Player("Player 4");
//        game.getPlayers().addAll(Arrays.asList(player1, player2, player3, player4));
//        game.startRound();
//        assertFalse(game.getTopCard()==null);
//        assertFalse(player1.getHand().contains(null));
//        assertFalse(player2.getHand().contains(null));
//        assertFalse(player3.getHand().contains(null));
//        assertFalse(player4.getHand().contains(null));
//        assertEquals(5, player1.getHand().size());
//        assertEquals(5, player2.getHand().size());
//        assertEquals(5, player3.getHand().size());
//        assertEquals(5, player4.getHand().size());
//        assertEquals(31, game.getNumCards());
//    }

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

    @Test
    public void canPlayFromHand(){
        //The player's hand contains an 8
        game.setTopCard(new Card(Rank.FOUR, Suit.HEARTS));
        player1.getHand().addAll(Arrays.asList(new Card(Rank.EIGHT, Suit.CLUBS)));
        assertTrue(game.canPlayFromHand(player1));

        //The player's hand contains a card wih the same suit
        player2.getHand().addAll(Arrays.asList(new Card(Rank.SIX, Suit.HEARTS), new Card(Rank.THREE, Suit.DIAMONDS)));
        assertTrue(game.canPlayFromHand(player2));

        //The player's hand contains a card with the same rank
        player3.getHand().addAll(Arrays.asList(new Card(Rank.FOUR, Suit.CLUBS), new Card(Rank.THREE, Suit.DIAMONDS)));
        assertTrue(game.canPlayFromHand(player3));

        //The player's hand does not contain a card with the same rank or suit
        player4.getHand().addAll(Arrays.asList(new Card(Rank.TWO, Suit.SPADES), new Card(Rank.THREE, Suit.DIAMONDS)));
        assertFalse(game.canPlayFromHand(player4));
    }

    @Test
    public void testUpdateTurn(){
        //Player 1 reverses the game direction by playing an ace
        game.setTurn(1);
        game.setTopCard(new Card(Rank.FOUR, Suit.HEARTS));
        player1.getHand().addAll(Arrays.asList(new Card(Rank.ACE, Suit.HEARTS)));
        game.playCard(player1,new Card(Rank.ACE, Suit.HEARTS));
        assertEquals(4, game.updateTurn());

        //Player 4 plays and the next player should be player 3
        player4.getHand().addAll(Arrays.asList(new Card(Rank.TWO, Suit.HEARTS)));
        game.playCard(player4,new Card(Rank.TWO, Suit.HEARTS));
        assertEquals(3, game.updateTurn());

        //Player 3 plays and reverses the game direction by playing an ace
        player3.getHand().addAll(Arrays.asList(new Card(Rank.ACE, Suit.HEARTS)));
        game.playCard(player3,new Card(Rank.ACE, Suit.HEARTS));
        assertEquals(4, game.updateTurn());

        //Player 4 plays and the next player should be player 1
        player4.getHand().addAll(Arrays.asList(new Card(Rank.TWO, Suit.HEARTS)));
        game.playCard(player4,new Card(Rank.TWO, Suit.HEARTS));
        assertEquals(1, game.updateTurn());

        //Player 1 plays a queen and the next player should be player 3
        player1.getHand().addAll(Arrays.asList(new Card(Rank.QUEEN, Suit.HEARTS)));
        game.playCard(player1,new Card(Rank.QUEEN, Suit.HEARTS));
        assertEquals(3, game.updateTurn());

    }

    @Test
    public void testCanPlayNCardsFromHand(){
        game.setTopCard(new Card(Rank.FOUR, Suit.HEARTS));
        player1.getHand().addAll(Arrays.asList(new Card(Rank.SIX, Suit.CLUBS)));
        assertTrue(game.canPlayFromHand(player1, 0));

        player1.getHand().addAll(Arrays.asList(new Card(Rank.EIGHT, Suit.CLUBS)));
        assertTrue(game.canPlayFromHand(player1, 1));

        //The player's hand contains a card wih the same suit
        player2.getHand().addAll(Arrays.asList(new Card(Rank.SIX, Suit.HEARTS), new Card(Rank.THREE, Suit.HEARTS)));
        assertTrue(game.canPlayFromHand(player2, 2));

        //The player's hand does not contain a card with the same rank or suit
        player3.getHand().addAll(Arrays.asList(new Card(Rank.TEN, Suit.HEARTS), new Card(Rank.EIGHT, Suit.SPADES), new Card(Rank.FOUR, Suit.DIAMONDS)));
        assertTrue(game.canPlayFromHand(player3, 3));

        //The player's hand contains a card with the same rank
        player4.getHand().addAll(Arrays.asList(new Card(Rank.FOUR, Suit.CLUBS), new Card(Rank.THREE, Suit.HEARTS), new Card(Rank.FOUR, Suit.SPADES), new Card(Rank.FIVE,Suit.HEARTS)));
        assertTrue(game.canPlayFromHand(player4, 4));

    }

    @Test
    public void testCalculateScore(){
        ArrayList<Card> playerOneHand = new ArrayList<>();
        for(int i = 0; i < Rank.values().length; i++){
            Card card = new Card(Rank.values()[i], Suit.HEARTS);
            playerOneHand.add(card);
        }

        int expectedScore = 1+2+3+4+5+6+7+50+9+10+10+10+10;

        assertEquals(expectedScore,game.calculateScore(playerOneHand));
    }

    @Test
    public void testGetRoundWinner(){
        player1.setRoundScore(0);
        player2.setRoundScore(50);
        player3.setRoundScore(30);
        player4.setRoundScore(20);
        game.getPlayers().addAll(Arrays.asList(player1, player2, player3, player4));
        assertEquals(player1, game.getRoundWinner());
    }
}