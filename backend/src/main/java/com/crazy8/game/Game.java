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
    private int turn;

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

    public void setTopCard(Card topCard) {
        this.topCard = topCard;
    }

    public void updateCardCount(Card card){
        if(card.getSuit() == Suit.DIAMONDS){
            numDiamondsCards--;
        }

        else if(card.getSuit() == Suit.CLUBS){
            numClubsCards--;
        }

        else if(card.getSuit() == Suit.SPADES){
            numSpadesCards--;
        }

        else{
            numHeartsCards--;
        }
        numCards--;
    }

    public Card drawCard(Player player){
        if(deck.getDeck().isEmpty()){
            return null;
        }
        Card topStockpileCard = deck.getDeck().remove(0);
        player.getHand().add(topStockpileCard);
        updateCardCount(topStockpileCard);
        return topStockpileCard;
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
        updateCardCount(topCard);
        return newTopCard;
    }

    public boolean startRound(){
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

    public Card playCard(Player player, Card card){
        if(!player.getHand().contains(card)){
            return null;
        }

        System.out.println("I got here with this card: " + card);
        if(topCard.getRank() == card.getRank() || topCard.getSuit() == card.getSuit() || card.getRank() == Rank.EIGHT){
            setTopCard(card);
            player.getHand().remove(card);
            return card;
        }

        return null;
    }

}
