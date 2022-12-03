package com.crazy8.server;
import com.crazy8.game.Card;
import com.crazy8.game.Player;
import com.crazy8.game.Game;
import com.crazy8.server.Defs.Action;
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

    @MessageMapping("/message")  //app/message
    @SendTo("/playroom/public")
    private ServerMessage receivePublicMessage(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        if(message.getAction() == Action.JOIN){
            Player player = new Player(message.getName(),0);
            game.getPlayers().add(player);
            player.setId(game.getPlayers().size());
            response.setId(Integer.toString(player.getId()));
            response.setMessage("Player " + (player.getId()) + " (" + player.getName() + ")" + " has joined." );
        }



        return response;
    }

    @MessageMapping("/private-message")
    public ServerMessage receivePrivateMessage(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        Player player = null;
        for(Player p: game.getPlayers()){
            if(p.getName().equalsIgnoreCase(message.getName())){
                player = p;
            }
        }

        if(message.getAction() == Action.DRAW && player != null){
            Card drawnCard = game.drawCard(player);
            if(drawnCard != null){
                String drawnCardSuit = drawnCard.getSuit().toString().toLowerCase();
                String drawnCardRank = drawnCard.getRank().toString();
                response.setCards("{\"suit\": \"" + drawnCardSuit+ "\", \"rank\": \""+ drawnCardRank  +"\"}" );
            }

        }

        System.out.println(response.toString());
        simpMessagingTemplate.convertAndSendToUser(response.getName(), "/private", response); //user/Jola/private

        return response;
    }

}