package com.crazy8.game;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Player {
    private String name;
    private int roundScore;

    private int gameScore;
    private int id;
    private List<Card> hand;
    private int numCardDraws;

    public Player(String name) {
        this.name = name;
        roundScore = 0;
        gameScore = 0;
        hand = new ArrayList<>();
        id = -1;
        numCardDraws = 0;
    }

    public Player(){
        name = "";
        roundScore = 0;
        gameScore = 0;
        id = -1;
        hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoundScore() {
        return roundScore;
    }

    public void setRoundScore(int roundScore) {
        this.roundScore = roundScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public int getGameScore() {
        return gameScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore = gameScore;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", roundScore=" + roundScore +
                ", gameScore=" + gameScore +
                ", id=" + id +
                ", hand=" + hand +
                ", numCardDraws=" + numCardDraws +
                '}';
    }
}
