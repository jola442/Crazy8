package com.crazy8.game;

import org.junit.jupiter.api.Test;
import com.crazy8.game.Defs.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameUnitTest {
    Game game = new Game();
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
        game.start();
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
}