package com.crazy8.game;

import org.junit.jupiter.api.Test;
import com.crazy8.game.Defs.*;
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
}