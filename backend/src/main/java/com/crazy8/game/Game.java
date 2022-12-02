package com.crazy8.game;
import com.crazy8.game.Defs.*;

public class Game {
    private Deck deck;
    private int numCards;
    private int numSpadesCards;
    private int numHeartsCards;
    private int numDiamondsCards;
    private int numClubsCards;

    public Game(){
        deck = new Deck();
        numCards = Defs.NUM_CARDS;
        numSpadesCards = Rank.values().length;
        numDiamondsCards = Rank.values().length;
        numClubsCards = Rank.values().length;
        numHeartsCards = Rank.values().length;
    }

    public int getNumCards() {
        return numCards;
    }


    public int getNumSpadesCards() {
        return numSpadesCards;
    }

    public int getNumHeartsCards() {
        return numHeartsCards;
    }

    public int getNumDiamondsCards() {
        return numDiamondsCards;
    }

    public Card drawCard(){
        Card topCard = deck.getDeck().get(0);
        if(topCard.getSuit() == Suit.DIAMONDS){
            numDiamondsCards--;
        }

        else if(topCard.getSuit() == Suit.CLUBS){
            numClubsCards--;
        }

        else if(topCard.getSuit() == Suit.SPADES){
            numSpadesCards--;
        }

        else{
            numHeartsCards--;
        }
        numCards--;
        return deck.getDeck().remove(0);
    }

    public int getNumClubsCards() {
        return numClubsCards;
    }
}
