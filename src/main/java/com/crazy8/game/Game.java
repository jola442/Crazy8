package com.crazy8.game;
import com.crazy8.game.Defs.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class Game {
    private Deck deck;
    private int numCards;
    private int numSpadesCards;
    private int numHeartsCards;
    private int numDiamondsCards;
    private int numClubsCards;
    private int numStackedTwoCards;

    private int numCardsDrawn;
//    private int numPlayerOneInitialCards;
//    private int numPlayerTwoInitialCards;
//    private int numPlayerThreeInitialCards;
//    private int numPlayerFourInitialCards;
//
    private ArrayList<Integer> numPlayerInitialCards;
    private List<Player> players;
    private Card topCard;
    private int turn;

    private Direction direction;
    private int roundNum;
    private boolean endOfRound;

    private ArrayList<Deck> riggedDecks;

    private boolean riggedRound;


    public Game(){
        deck = new Deck();
        numCards = Defs.NUM_CARDS;
        numSpadesCards = Rank.values().length;
        numDiamondsCards = Rank.values().length;
        numClubsCards = Rank.values().length;
        numHeartsCards = Rank.values().length;
        players = new ArrayList<>();
        topCard = null;
        direction = Direction.RIGHT;
        turn = 0;
        numStackedTwoCards = 0;
        numCardsDrawn = 0;
//        numPlayerOneInitialCards = 5;
//        numPlayerTwoInitialCards = 5;
//        numPlayerThreeInitialCards = 5;
//        numPlayerFourInitialCards = 5;
        numPlayerInitialCards = new ArrayList(Arrays.asList(5,5,5,5));
        roundNum = 0;
        endOfRound = false;
        riggedDecks = new ArrayList<>();
        riggedRound = false;
    }


    public int getRoundNum() {
        return roundNum;
    }

    public void setRoundNum(int roundNum) {
        this.roundNum = roundNum;
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

    public void setPlayers(List<Player> players) {
        this.players = players;
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


    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getNumStackedTwoCards() {
        return numStackedTwoCards;
    }

    public void setNumStackedTwoCards(int numStackedTwoCards) {
        this.numStackedTwoCards = numStackedTwoCards;
    }

    public ArrayList<Integer> getNumPlayerInitialCards() {
        return numPlayerInitialCards;
    }

    public void setNumPlayerInitialCards(ArrayList<Integer> numPlayerInitialCards) {
        this.numPlayerInitialCards = numPlayerInitialCards;
    }

    public int getNumCardsDrawn() {
        return numCardsDrawn;
    }

    public void setNumCardsDrawn(int numCardsDrawn) {
        this.numCardsDrawn = numCardsDrawn;
    }


    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }

    public boolean isEndOfRound() {
        return endOfRound;
    }

    public void setEndOfRound(boolean endOfRound) {
        this.endOfRound = endOfRound;
    }

    public ArrayList<Deck> getRiggedDecks() {
        return riggedDecks;
    }

    public void setRiggedDecks(ArrayList<Deck> riggedDecks) {
        this.riggedDecks = riggedDecks;
    }

    public boolean isRiggedRound() {
        return riggedRound;
    }

    public void setRiggedRound(boolean riggedRound) {
        this.riggedRound = riggedRound;
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
        if(deck.getCards().isEmpty()){
            return null;
        }
        Card topStockpileCard = deck.getCards().remove(0);
        player.getHand().add(topStockpileCard);
        updateCardCount(topStockpileCard);
        return topStockpileCard;
    }

    public Card drawCard(){
        if(deck.getCards().isEmpty()){
            return null;
        }
        Card topStockpileCard = deck.getCards().remove(0);
        updateCardCount(topStockpileCard);
        return topStockpileCard;
    }

    public Card placeStartingCard(){
        if(deck.getCards().isEmpty()){
            return null;
        }
        Card newTopCard = deck.getCards().remove(0);

        while(newTopCard.getRank() == Rank.EIGHT || newTopCard.getRank() == Rank.ACE || newTopCard.getRank() == Rank.QUEEN || newTopCard.getRank() == Rank.TWO){
            Collections.shuffle(deck.getCards());
        }
        topCard = newTopCard;
        updateCardCount(topCard);
        return newTopCard;
    }



    public int updateTurn(){
        numCardsDrawn = 0;
//        System.out.println("CODE: turn before updateTurn(): " + turn);
        if(topCard.getRank() == Rank.QUEEN){
            if(direction == Direction.LEFT){
                if(turn == 4){
                    turn = 2;
                }

                else if(turn == 3){
                    turn = 1;
                }

                else{
                    turn += 2;
                }
            }

            else{
                if(turn == 1){
                    turn = 3;
                }

                else if(turn == 2){
                    turn = 4;
                }

                else{
                    turn -= 2;
                }
            }
//            System.out.println("CODE: turn after updateTurn(): " + turn);
            return turn;
        }

        else if(topCard.getRank() == Rank.ACE){
            if(direction == Direction.RIGHT){
                direction = Direction.LEFT;
            }

            else{
                direction = Direction.RIGHT;
            }
        }

        if(direction == Direction.RIGHT){
            turn += 1;
            turn = turn > 4? 1:turn;
        }

        else{
            turn -= 1;
            turn = turn < 1?4:turn;
        }
//        System.out.println("CODE: turn after updateTurn(): " + turn);
        return turn;
    }

    public void updateRoundScores(){
        for(int i = 0; i < players.size(); ++i){
            Player player = players.get(i);
            player.setRoundScore(calculateScore(player.getHand()));
        }
    }
    public void updateGameScores(){
        for(int i = 0; i < players.size(); ++i){
            Player player = players.get(i);
            player.setGameScore(player.getGameScore()+player.getRoundScore());
        }
    }

    public Card playCard(Player player, Card card){
        if(!player.getHand().contains(card)){
            return null;
        }

        if(topCard.getRank() == card.getRank() || topCard.getSuit() == card.getSuit() || card.getRank() == Rank.EIGHT){
            setTopCard(card);
            if(topCard.getRank() == Rank.TWO){
                numStackedTwoCards++;
            }

            else{
                numStackedTwoCards = 0;
            }

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

    public boolean canPlayFromHand(Player player, int numCards){
        int canBePlayed = 0;
        for(Card card:player.getHand()){
            if(topCard.getRank() == card.getRank() || topCard.getSuit() == card.getSuit() || card.getRank() == Rank.EIGHT){
                canBePlayed++;
            }
        }

        return canBePlayed == numCards;
    }

    public int calculateScore(List<Card> playerHand){
//        System.out.println("Calculating the score for this hand: " + playerHand);
        int score = 0;
        for(int i = 0; i < playerHand.size(); ++i){
            Card card = playerHand.get(i);
            if(card.getRank() ==  Rank.EIGHT){
                score += 50;
            }

            else if(card.getRank() == Rank.JACK || card.getRank() == Rank.QUEEN || card.getRank() == Rank.KING){
                score += 10;
            }

            else
                if(card.getRank() == Rank.ACE){
                    score += 1;
                }

                else {
                    score += Integer.parseInt(card.getRank().toString());
                }
            }
//        System.out.println("The score is " + score);
            return score;
        }


    public Player getRoundWinner(){
        ArrayList<Integer> playersScores = new ArrayList<>();
        for(int i = 0; i < players.size(); ++i){
//            System.out.println("CODE: Player " + i + " has a score of " + players.get(i).getRoundScore());
            playersScores.add(players.get(i).getRoundScore());
        }

        int winnerScore = Collections.min(playersScores);
        for(int i = 0; i < players.size(); ++i){
            if(players.get(i).getRoundScore() == winnerScore){
                return players.get(i);
            }
        }
        return null;
    }

    public Player getGameWinner(){
        ArrayList<Integer> playersScores = new ArrayList<>();
        for(int i = 0; i < players.size(); ++i){
            playersScores.add(players.get(i).getGameScore());
        }

        boolean gameHasWinner = Collections.max(playersScores) >= 100;

        if(!gameHasWinner){
            return null;
        }

        int winnerScore = Collections.min(playersScores);
        for(int i = 0; i < players.size(); ++i){
            if(players.get(i).getGameScore() == winnerScore){
                return players.get(i);
            }
        }

        return null;
    }



    public void resetState(){
        deck = new Deck();
        deck.getCards().clear();
        players = new ArrayList<>();
        numCards = Defs.NUM_CARDS;
        numSpadesCards = Rank.values().length;
        numDiamondsCards = Rank.values().length;
        numClubsCards = Rank.values().length;
        numHeartsCards = Rank.values().length;
        topCard = null;
        direction = Direction.RIGHT;
        turn = 1;
        numStackedTwoCards = 0;
        roundNum = 0;
        endOfRound = false;
        riggedRound = true;
    }


}


