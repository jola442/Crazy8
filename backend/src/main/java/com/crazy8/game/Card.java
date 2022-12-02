package com.crazy8.game;
import com.crazy8.game.Defs.Rank;
import com.crazy8.game.Defs.Suit;

public class Card {
    private Suit suit;
    private Rank rank;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Suit getSuit() {
        return suit;
    }

}
