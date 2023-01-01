package com.crazy8.game;
import com.crazy8.game.Defs.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private int numPlayerOneInitialCards;
    private int numPlayerTwoInitialCards;
    private int numPlayerThreeInitialCards;
    private int numPlayerFourInitialCards;
    private List<Player> players;
    private Card topCard;
    private int turn;

    private Direction direction;
    private int roundNum;
    private boolean endOfRound;


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
        turn = 1;
        numStackedTwoCards = 0;
        numPlayerOneInitialCards = 5;
        numPlayerTwoInitialCards = 5;
        numPlayerThreeInitialCards = 5;
        numPlayerFourInitialCards = 5;
        roundNum = 0;
        endOfRound = false;
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

    public int getNumStackedTwoCards() {
        return numStackedTwoCards;
    }

    public void setNumStackedTwoCards(int numStackedTwoCards) {
        this.numStackedTwoCards = numStackedTwoCards;
    }

    public int getNumPlayerOneInitialCards() {
        return numPlayerOneInitialCards;
    }

    public void setNumPlayerOneInitialCards(int numPlayerOneInitialCards) {
        this.numPlayerOneInitialCards = numPlayerOneInitialCards;
    }

    public int getNumPlayerTwoInitialCards() {
        return numPlayerTwoInitialCards;
    }

    public void setNumPlayerTwoInitialCards(int numPlayerTwoInitialCards) {
        this.numPlayerTwoInitialCards = numPlayerTwoInitialCards;
    }

    public int getNumPlayerThreeInitialCards() {
        return numPlayerThreeInitialCards;
    }

    public void setNumPlayerThreeInitialCards(int numPlayerThreeInitialCards) {
        this.numPlayerThreeInitialCards = numPlayerThreeInitialCards;
    }

    public int getNumPlayerFourInitialCards() {
        return numPlayerFourInitialCards;
    }

    public void setNumPlayerFourInitialCards(int numPlayerFourInitialCards) {
        this.numPlayerFourInitialCards = numPlayerFourInitialCards;
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

        while(newTopCard.getRank() == Rank.EIGHT){
            Collections.shuffle(deck.getCards());
        }
        topCard = newTopCard;
        updateCardCount(topCard);
        return newTopCard;
    }

//    public boolean startRound(){
//        roundNum ++;
//        deck = new Deck();
//        for(Player player: players){
//            player.getHand().clear();
//            for(int i = 0; i < Defs.NUM_STARTING_CARDS; ++i){
//                if(drawCard(player) == null){
//                    return false;
//                }
//            }
//        }
//
//        placeStartingCard();
//        return true;
//    }

    public int updateTurn(){
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
            return score;
        }

    public Player getRoundWinner(){
        ArrayList<Integer> playersScores = new ArrayList<>();
        for(int i = 0; i < players.size(); ++i){
            playersScores.add(players.get(i).getScore());
        }

        int winnerScore = Collections.min(playersScores);
        for(int i = 0; i < players.size(); ++i){
            if(players.get(i).getScore() == winnerScore){
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

    public Player getGameWinner(){
        boolean gameIsOver = false;
        int winnerScore = players.get(0).getScore();
        int winnerIndex = 0;

        for(int i = 0; i < players.size(); ++i){
            int playerScore = players.get(i).getScore();

            if(playerScore >= 100){
                gameIsOver = true;
            }

            if(playerScore < winnerScore){
                winnerScore = playerScore;
                winnerIndex = i;
            }
        }

        if(gameIsOver){
            return players.get(winnerIndex);
        }

        else{
            return null;
        }

    }


}


