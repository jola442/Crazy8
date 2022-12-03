package com.crazy8.server;
import com.crazy8.game.Card;
import com.crazy8.game.Player;
import com.crazy8.game.Game;
import com.crazy8.server.Defs.Action;
import com.crazy8.game.Defs;
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
            String cardSuit = card.getSuit().toString().toLowerCase();
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

    private ServerMessage handleUserJoining(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));

        //Creating the player model
        Player player = new Player(message.getName());
        game.getPlayers().add(player);
        player.setId(game.getPlayers().size());

        //Configuring the server response
        response.setId(Integer.toString(player.getId()));
        response.setMessage("Player " + (player.getId()) + " (" + player.getName() + ")" + " has joined." );
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));

        return response;
    }

    private ServerMessage handleSendingTopCard(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));
        ArrayList<Card> startingCard = new ArrayList<>();
        startingCard.add(game.placeStartingCard());
        response.setCards(stringifyCards(startingCard));
        response.setAction(Action.DRAW);
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
        response.setAction(Action.JOIN);
        if(player == null){
            response.setId(Integer.toString(game.getPlayers().size()+1));
        }

        else{
            response.setId(Integer.toString(player.getId()));
        }

        return response;
    }

    private  ServerMessage  handleSendingStartingCards(@Payload ClientMessage message, Player player) {
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setAction(Action.DRAW);
        for(int i = 0; i < Defs.NUM_STARTING_CARDS; ++i){
            game.drawCard(player);
        }

        response.setCards(stringifyCards(player.getHand()));
        response.setMessage(message.getMessage());
        return response;
    }

    private ServerMessage handleSendingDrawnCard(@Payload ClientMessage message, Player player) {
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
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

        if(message.getAction() == Action.DRAW && player != null){
            if(message.getMessage().equalsIgnoreCase("starting cards")){
                response = handleSendingStartingCards(message, player);
            }

            else{
                response = handleSendingDrawnCard(message, player);
            }


        }

        System.out.println(response.toString());
        simpMessagingTemplate.convertAndSendToUser(response.getName(), "/private", response); //user/Jola/private

        return response;
    }

}