package com.crazy8.server;
import com.crazy8.game.Card;
import com.crazy8.game.Player;
import com.crazy8.game.Game;
import com.crazy8.server.Defs.Action;
import static com.crazy8.game.Defs.NUM_STARTING_CARDS;
import com.crazy8.game.Defs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.ArrayList;
import java.util.List;



@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Game game = new Game();

    public String stringifyCards(List<Card> cards){
        String cardsString = "[";
        for(int i = 0; i < cards.size(); ++i){
            Card card = cards.get(i);
            String cardSuit = card.getSuit().toString();
            String cardRank = card.getRank().toString();

            if(i < cards.size()-1){
                cardsString +="{\"suit\": \"" + cardSuit+ "\", \"rank\": \""+ cardRank  +"\"},";
            }

            else{
                cardsString +="{\"suit\": \"" + cardSuit+ "\", \"rank\": \""+ cardRank  +"\"}]";
            }

        }
        return cardsString;
    }

    public Card decodeCard(String cardFromClient){
        int cardRankInt = 0;
        Rank cardRank = null;
        Suit cardSuit = null;
        int suitIndex = 0;
        if(cardFromClient.length() > 2){
            String rankString = "";
            rankString += cardFromClient.charAt(0);
            rankString += cardFromClient.charAt(1);
            cardRankInt = Integer.parseInt(rankString);
            cardRank = Rank.values()[cardRankInt-1];
            suitIndex = 2;
        }

        else{
            cardRankInt = Character.getNumericValue(cardFromClient.charAt(0));
            cardRank = Rank.values()[cardRankInt-1];
            suitIndex = 1;
        }
        switch (cardFromClient.charAt(suitIndex)){
            case 'H':
                cardSuit = Suit.HEARTS;
                break;
            case 'S':
                cardSuit = Suit.SPADES;
                break;
            case 'D':
                cardSuit = Suit.DIAMONDS;
                break;
            case 'C':
                cardSuit = Suit.CLUBS;
                break;
        }
        System.out.println("This should be a queen:" + Rank.values()[11]);
        System.out.println("Card rank: " + cardRank);
        System.out.println("Creating this card:" + cardRank.toString() + cardSuit.toString());
        return new Card(cardRank, cardSuit);

    }

    private ServerMessage handleUserJoining(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurn(Integer.toString(game.getTurn()));
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));

        //Creating the player model
        Player player = new Player(message.getName());
        game.getPlayers().add(player);
        player.setId(game.getPlayers().size());

        //Configuring the server response
        response.setId(Integer.toString(player.getId()));
        response.setMessage("Player " + (player.getId()) + " (" + player.getName() + ")" + " has joined." );
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));

        if(game.getPlayers().size() == 4){
            game.setTurn(1);
            response.setTurn(Integer.toString(game.getTurn()));
        }
        return response;
    }

    private ServerMessage handleSendingTopCard(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurn(Integer.toString(game.getTurn()));
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));

        ArrayList<Card> newTopCard = new ArrayList<>();
        if(game.getTopCard() == null){
            newTopCard.add(game.placeStartingCard());
        }

        else{
            newTopCard.add(game.getTopCard());
        }

        response.setAction(Action.DRAW);
        response.setCards(stringifyCards(newTopCard));


        return response;
    }


    @MessageMapping("/message")  //app/message
    @SendTo("/playroom/public")
    private ServerMessage receivePublicMessage(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        if(message.getAction() == Action.JOIN){
            response = handleUserJoining(message);
        }

        else if(message.getAction() == Action.DRAW){
            response = handleSendingTopCard(message);
        }

        return response;
    }

    public ServerMessage handleSendingUserID(@Payload ClientMessage message, Player player){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurn(Integer.toString(game.getTurn()));
        response.setAction(Action.JOIN);
        if(player == null){
            response.setId(Integer.toString(game.getPlayers().size()+1));
        }

        else{
            response.setId(Integer.toString(player.getId()));
        }

        return response;
    }

    private  ServerMessage handleSendingStartingCards(@Payload ClientMessage message, Player player) {
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurn(Integer.toString(game.getTurn()));
        response.setAction(Action.DRAW);
        for(int i = 0; i < NUM_STARTING_CARDS; ++i){
            game.drawCard(player);
        }

        response.setCards(stringifyCards(player.getHand()));
        response.setMessage(message.getMessage());
        return response;
    }

    private ServerMessage handleSendingDrawnCard(@Payload ClientMessage message, Player player) {
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurn(Integer.toString(game.getTurn()));
        response.setAction(Action.DRAW);
        Card drawnCard = game.drawCard(player);
        if(drawnCard != null){
            ArrayList<Card> cardsToSend = new ArrayList<>();
            cardsToSend.add(drawnCard);
            response.setCards(stringifyCards(cardsToSend));
        }

//        System.out.println(response.toString());
//        simpMessagingTemplate.convertAndSendToUser(response.getName(), "/private", response); //user/Jola/private
        return response;
    }

    private ServerMessage handlePlayingACard(@Payload ClientMessage message, Player player){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurn(Integer.toString(game.getTurn()));
        response.setAction(Action.PLAY);
        System.out.println(message);
        System.out.println("The card i'm getting from client: "+message.getMessage());

        if(message.getMessage().equalsIgnoreCase("")){
            response.setMessage("no");
        }

        else{
            Card card = decodeCard(message.getMessage());
            if(game.playCard(player, card) == null){
                response.setMessage("no");
            }
            else{
                response.setMessage("yes");
                response.setCards(stringifyCards(player.getHand()));
            }
        }
        return response;
    }

    @MessageMapping("/private-message")
    public ServerMessage receivePrivateMessage(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();

        Player player = null;
        for(Player p: game.getPlayers()){
            if(p.getName().equalsIgnoreCase(message.getName())){
                player = p;
            }
        }

        if(message.getAction() == Action.JOIN){
            response = handleSendingUserID(message,player);
        }

        else if(message.getAction() == Action.DRAW && player != null){
            if(message.getMessage().equalsIgnoreCase("starting cards")){
                response = handleSendingStartingCards(message, player);
            }

            else{
                response = handleSendingDrawnCard(message, player);
            }

        }

        else if(message.getAction() == Action.PLAY){
            response = handlePlayingACard(message, player);
        }

        System.out.println(response.toString());
        simpMessagingTemplate.convertAndSendToUser(response.getName(), "/private", response); //user/Jola/private

        return response;
    }

}

