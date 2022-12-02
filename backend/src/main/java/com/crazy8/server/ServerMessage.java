package com.crazy8.server;
import com.crazy8.server.Defs.Action;

public class ServerMessage {
    private String id;
    private String name;
    private String cards;
    private String message;
    private String scores;
    private Action action;
    private String gameDirection;
    private String turn;
    private String roundNum;



    public ServerMessage(){
        name = "";
        id = "0";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAction(Defs.Action action) {
        this.action = action;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public Action getAction() {
        return action;
    }

    public String getGameDirection() {
        return gameDirection;
    }

    public void setGameDirection(String gameDirection) {
        this.gameDirection = gameDirection;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getRoundNum() {
        return roundNum;
    }

    public void setRoundNum(String roundNum) {
        this.roundNum = roundNum;
    }
}
