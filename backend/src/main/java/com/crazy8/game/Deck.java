package com.crazy8.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.crazy8.game.Defs.*;

public class Deck {
    private List<Card> deck;

    public Deck(){
        deck = new ArrayList<>();

        for(int i = 0; i < Rank.values().length; ++i){
            for(int j = 0; j < Suit.values().length; ++j){
                Card card = new Card(Rank.values()[i], Suit.values()[j]);
            }
        }

        Collections.shuffle(deck);
    }

    public List<Card> getDeck() {
        return deck;
    }

}