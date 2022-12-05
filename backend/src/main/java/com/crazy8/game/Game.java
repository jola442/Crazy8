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
    private Direction direction;

    public Game(){
        deck = new Deck();
        numCards = Defs.NUM_CARDS;
        numSpadesCards = Rank.values().length;
        numDiamondsCards = Rank.values().length;
        numClubsCards = Rank.values().length;
        numHeartsCards = Rank.values().length;
        players = new ArrayList<>();
        topCard = null;
        direction = Direction.LEFT;
        turn = 0;
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

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Direction getDirection() {
        return direction;
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
        Card newTopCard = deck.getDeck().remove(0);
        System.out.println(deck);
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

    public int updateTurn(){
        System.out.println(topCard);
        if(topCard.getRank() == Rank.QUEEN){
            if(direction == Direction.LEFT){
                turn += 1;
            }

            else{
                turn -=1;
            }
        }

        else if(topCard.getRank() == Rank.ACE){
            if(direction == Direction.LEFT){
                direction = Direction.RIGHT;
            }

            else{
                direction = Direction.LEFT;
            }
        }

        if(direction == Direction.LEFT){
            turn += 1;
            turn = turn > 4? 1:turn;
        }

        else{
            turn -= 1;
            turn = turn < 1?4:turn;
        }
        return turn;
    }

    public Card playCard(Player player, Card card){
        if(!player.getHand().contains(card)){
            return null;
        }

        if(topCard.getRank() == card.getRank() || topCard.getSuit() == card.getSuit() || card.getRank() == Rank.EIGHT){
            setTopCard(card);
            player.getHand().remove(card);
            return card;
        }
        return null;
    }

    public boolean canPlayFromHand(Player player){
        for(Card card:player.getHand()){
            if(topCard.getRank() == card.getRank() || topCard.getSuit() == card.getSuit() || card.getRank() == Rank.EIGHT){
                return true;
            }
        }
        return false;
    }

}
