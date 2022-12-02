package com.crazy8.game;

public class Player {
    private String name;
    private int score;
    private int id;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
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
}
