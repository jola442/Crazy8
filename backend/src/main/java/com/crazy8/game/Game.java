package com.crazy8.game;
import com.crazy8.game.Defs.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private Deck deck;
    private int numCards;
    private int numSpadesCards;
    private int numHeartsCards;
    private int numDiamondsCards;
    private int numClubsCards;
    List<Player> players;
    private Card topCard;

    public Game(){
        deck = new Deck();
        numCards = Defs.NUM_CARDS;
        numSpadesCards = Rank.values().length;
        numDiamondsCards = Rank.values().length;
        numClubsCards = Rank.values().length;
        numHeartsCards = Rank.values().length;
        players = new ArrayList<>();
        topCard = null;
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

    public Card getTopCard() {
        return topCard;
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

    public Card placeStartingCard(){
        if(deck.getDeck().isEmpty()){
            return null;
        }
        Card newTopCard = deck.getDeck().get(0);
        while(newTopCard.getRank() == Rank.EIGHT){
            Collections.shuffle(deck.getDeck());
        }
        topCard = newTopCard;
        return newTopCard;
    }

    public boolean start(){
        if(numCards < 21){
            return false;
        }

        for(Player player: players){
            for(int i = 0; i < Defs.NUM_STARTING_CARDS; ++i){
                if(drawCard(player) == null){
                    return false;
                }
            }
        }

        placeStartingCard();
        return true;


    }

}
