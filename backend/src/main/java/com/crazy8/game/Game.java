package com.crazy8.game;
import com.crazy8.game.Defs.*;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Deck deck;
    private int numCards;
    private int numSpadesCards;
    private int numHeartsCards;
    private int numDiamondsCards;
    private int numClubsCards;
    List<Player> players;


    public Game(){
        deck = new Deck();
        numCards = Defs.NUM_CARDS;
        numSpadesCards = Rank.values().length;
        numDiamondsCards = Rank.values().length;
        numClubsCards = Rank.values().length;
        numHeartsCards = Rank.values().length;
        players = new ArrayList<>();
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

    public int getNumClubsCards() {
        return numClubsCards;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Card drawCard(Player player){
        if(deck.getDeck().isEmpty()){
            return null;
        }
        Card topCard = deck.getDeck().remove(0);
        player.getHand().add(topCard);
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
        return topCard;
    }


}
