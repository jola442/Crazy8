package com.crazy8.server;
import com.crazy8.server.Defs.Action;

public class ClientMessage {
    private String id;
    private String name;
    private Action action;
    private String cards;
    private String message;

    public ClientMessage(){
        name = "";
        id = "0";
        action = Action.JOIN;
        cards = "";
        message = "";
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
