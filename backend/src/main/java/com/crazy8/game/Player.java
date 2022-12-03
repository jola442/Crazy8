package com.crazy8.game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int score;
    private int id;
    private List<Card> hand;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
        hand = new ArrayList<>();
    }

    public Player(){
        name = "";
        score = 0;
        id = -1;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
}
