package com.crazy8.server;
import com.crazy8.game.Card;
import com.crazy8.game.Player;
import com.crazy8.game.Game;
import com.crazy8.server.Defs.Action;
import com.crazy8.game.Defs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.ArrayList;
import java.util.List;

import static com.crazy8.game.Defs.*;


@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private Game game;

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

//        System.out.println("Card rank: " + cardRank);
        System.out.println("Creating this card:" + cardRank.toString() + cardSuit.toString());
        return new Card(cardRank, cardSuit);

    }

    private ServerMessage handleUserJoining(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurnNumber(Integer.toString(game.getTurn()));
        response.setDirection(game.getDirection());
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
            if(game.getTopCard() == null){
                game.placeStartingCard();
            }

            for(int i = 0; i < NUM_PLAYERS; ++i){
                for(int j = 0; j < game.getNumInitialCards(); ++j){
                    game.getPlayers().get(i).getHand().add(game.drawCard());
                }
            }

            game.setTurn(1);
            response.setTurnNumber(Integer.toString(game.getTurn()));
        }
        return response;
    }

    private ServerMessage handleSendingTopCard(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurnNumber(Integer.toString(game.getTurn()));
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));
        response.setAction(Action.UPDATE);
        response.setDirection(game.getDirection());

        ArrayList<Card> newTopCard = new ArrayList<>();

        if(game.getTopCard() != null){
            String msg = "";
            if(game.getTopCard().getRank() == Rank.ACE){
                if(game.getDirection() == Direction.LEFT){
                    int playerNumber = game.getTurn() == 1?4:game.getTurn()-1;
                    msg += "Player " + playerNumber + " changed the direction from " + Direction.RIGHT + " to " + Direction.LEFT + " by playing an ACE";
                }

                else{
                    int playerNumber = game.getTurn() == 4?1:game.getTurn()+1;
                    msg += "Player " + playerNumber + " changed the direction from " + Direction.LEFT + " to " + Direction.RIGHT + " by playing an ACE";
                }

            }

            else if(game.getTopCard().getRank() == Rank.QUEEN){
                int firstPlayer = 0;
                int secondPlayer = 0;

                if(game.getDirection() == Direction.LEFT){
                    if(game.getTurn() == 4 || game.getTurn() == 3){
                        firstPlayer = game.getTurn()-2;
                        secondPlayer = game.getTurn()-1;
                    }

                    else if(game.getTurn() == 2){
                        firstPlayer = game.getTurn()+2;
                        secondPlayer = game.getTurn()-1;
                    }

                    else{
                        System.out.println("CHECK: branch ran");
                        firstPlayer = game.getTurn()+2;
                        secondPlayer = game.getTurn()+3;
                    }
                }

                else{
                    if(game.getTurn() == 1 || game.getTurn() == 2){
                        firstPlayer = game.getTurn()+2;
                        secondPlayer = game.getTurn()+1;
                    }

                    else if(game.getTurn() == 3){
                        firstPlayer = game.getTurn()-2;
                        secondPlayer = game.getTurn()+1;
                    }

                    else{
                        firstPlayer = game.getTurn()-2;
                        secondPlayer = game.getTurn()-3;
                    }

                }
                System.out.println("CODE: Current Turn used to compute next turn: " + game.getTurn());
                msg += "Player " + firstPlayer + " played a QUEEN causing Player " +  secondPlayer + " to miss their turn";
                System.out.println("CODE: " + msg);

            }

            else if(game.getTopCard().getRank() == Rank.TWO){
                System.out.println("Increasing num 2s by 1 for player " + game.getTurn());
                game.setNumStackedTwoCards(game.getNumStackedTwoCards()+1);
            }

            else if(message.getMessage().equalsIgnoreCase("HEARTS")){
                System.out.println("CODE: SETTING THE TOP CARD TO HEARTS");
                game.setTopCard(new Card(Rank.EIGHT, Suit.HEARTS));
            }

            else if(message.getMessage().equalsIgnoreCase("SPADES")){
                System.out.println("CODE: SETTING THE TOP CARD TO HEARTS");
                game.setTopCard(new Card(Rank.EIGHT, Suit.SPADES));
            }
            else if(message.getMessage().equalsIgnoreCase("DIAMONDS")){
                System.out.println("CODE: SETTING THE TOP CARD TO DIAMONDS");
                game.setTopCard(new Card(Rank.EIGHT, Suit.DIAMONDS));
            }
            else if(message.getMessage().equalsIgnoreCase("CLUBS")){
                System.out.println("CODE: SETTING THE TOP CARD TO CLUBS");
                game.setTopCard(new Card(Rank.EIGHT, Suit.CLUBS));
            }


            response.setMessage(msg);
            newTopCard.add(game.getTopCard());
        }

        response.setCards(stringifyCards(newTopCard));


        return response;
    }

    private ServerMessage handleSendingStartingTopCard(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurnNumber(Integer.toString(game.getTurn()));
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));
        response.setDirection(game.getDirection());
        response.setMessage(message.getMessage());

        ArrayList<Card> newTopCard = new ArrayList<>();
        newTopCard.add(game.getTopCard());
//        if(game.getTopCard() == null){
//            newTopCard.add(game.placeStartingCard());
//        }
//
//        else{
//            newTopCard.add(game.getTopCard());
//        }

        response.setAction(Action.UPDATE);
        response.setCards(stringifyCards(newTopCard));
        System.out.println("Sending the starting top card to all players " + newTopCard);

        return response;
    }

    @MessageMapping("/message")  //app/message
    @SendTo("/playroom/public")
    private ServerMessage receivePublicMessage(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();
        if(message.getAction() == Action.JOIN){
            response = handleUserJoining(message);
        }

        else if(message.getAction() == Action.UPDATE){
            if(message.getMessage().equalsIgnoreCase("turn")){
                response.setMessage(message.getMessage());
                response.setTurnNumber(Integer.toString(game.getTurn()));
                response.setAction(Action.UPDATE);
            }

            else if(message.getMessage().equalsIgnoreCase("starting top card")){
                response = handleSendingStartingTopCard(message);
            }

            else{
                response = handleSendingTopCard(message);
            }

//            else if(message.getMessage().equalsIgnoreCase("top card")){
//                response.setCards(stringifyCards(new ArrayList<>(game.getTopCard())));
//            }
        }
        if(game.getPlayers().size() > 1 && game.getTurn() > 0){
            response.setCurrentPlayerTurn(game.getPlayers().get(game.getTurn()-1).getName());
        }

        return response;
    }

    public ServerMessage handleSendingUserID(@Payload ClientMessage message, Player player){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurnNumber(Integer.toString(game.getTurn()));
        response.setDirection(game.getDirection());
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
        response.setTurnNumber(Integer.toString(game.getTurn()));
        response.setDirection(game.getDirection());
        response.setAction(Action.DRAW);

        System.out.println("Sending starting cards to " + player.getName() + ": " + player.getHand());
        response.setCards(stringifyCards(player.getHand()));
        response.setMessage(message.getMessage());
        return response;
    }

    private ServerMessage handleSendingDrawnCard(@Payload ClientMessage message, Player player) {
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurnNumber(Integer.toString(game.getTurn()));
        response.setDirection(game.getDirection());

        //If the top card is a 2
        if(message.getMessage().equalsIgnoreCase("2 card")){
            int numCardsToPlay = TWO_CARD_PENALTY * game.getNumStackedTwoCards();
            boolean playerCanPlay = game.canPlayFromHand(player, numCardsToPlay);
            StringBuilder msg = new StringBuilder(game.getNumStackedTwoCards() + " player(s) played a 2 \n So you have to play " + numCardsToPlay + " cards or draw " + numCardsToPlay + " cards\n");
            if(playerCanPlay){
                response.setAction(Action.PLAY);
                System.out.println("determined that the user can play");

                int numCardsPlayed = 0;
                for(int i = 0; i < player.getHand().size(); ++i){
                    Card card = player.getHand().get(i);
                    if(numCardsPlayed == (TWO_CARD_PENALTY*game.getNumStackedTwoCards())){
                        break;
                    }
                    if(game.playCard(player, card) != null){
                        msg.append("You play a ").append(card.getRank()).append(" ").append(card.getSuit()).append("\n");
                        System.out.println(msg);
                        numCardsPlayed++;
                    }
                }

//                if the player's hand contains a 2
                if(player.getHand().contains(new Card(Rank.TWO, Suit.SPADES))||
                   player.getHand().contains(new Card (Rank.TWO, Suit.DIAMONDS))||
                   player.getHand().contains(new Card (Rank.TWO, Suit.DIAMONDS))||
                   player.getHand().contains(new Card (Rank.TWO, Suit.DIAMONDS))
                ){
                   game.setNumStackedTwoCards(game.getNumStackedTwoCards()+1);
                }

                else{
                    game.setNumStackedTwoCards(0);
                }

            }

            else{
                response.setAction(Action.DRAW);
                msg.append("You can't play up to ").append(numCardsToPlay).append(" cards \n So");
                System.out.println(numCardsToPlay);

                for(int i = 0; i < numCardsToPlay; i++){
                    Card card = game.drawCard(player);
                    msg.append("you draw a(n) ").append(card.getRank()).append(" ").append(card.getSuit()).append("\n");
                }
            }
            response.setMessage(msg.toString());
        }

        else{
            response.setAction(Action.DRAW);
            Card drawnCard = game.drawCard(player);
            if(drawnCard != null){
                ArrayList<Card> cardsToSend = new ArrayList<>();
                cardsToSend.add(drawnCard);
                response.setCards(stringifyCards(cardsToSend));
            }
        }
        response.setCards(stringifyCards(player.getHand()));
        return response;
    }

    private ServerMessage handlePlayingACard(@Payload ClientMessage message, Player player){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurnNumber(Integer.toString(game.getTurn()));
        response.setDirection(game.getDirection());
        response.setAction(Action.PLAY);


        if(message.getMessage().equalsIgnoreCase("")){
            response.setMessage("no");
        }

        else{
            Card card = decodeCard(message.getMessage());
            //If the selected card cannot be played
            if(game.playCard(player, card) == null){
              String msg = "You cannot play this card (" + card.getRank() + " " + card.getSuit() + ")\n";

              int numDrawnCards = 0;
              //Draw up to 3 cards and play
               for(int i = 0; i < MAX_NUM_DRAWS_PER_TURN; ++i){
                   //If the player cannot play any card from their hand
                   if(!game.canPlayFromHand(player)){
                       Card newCard = game.drawCard(player);
                       //Check if the player successfully draws a card
                       if(newCard != null){
                           msg += "You cannot play any other card from your hand \n So you draw a(n) " + newCard.getRank().toString() + " " + newCard.getSuit() + "\n";
                           numDrawnCards++;
                       }

                       else{
                           msg += "There are no cards left to draw\n";
                       }

                   }

                   //If the player can play a card from their hand
                   else{
                       //If they can play because they drew a card, they must play the card
                       if(numDrawnCards >= 1){
                           int handSize = player.getHand().size();
                           Card cardPlayed = game.playCard(player, player.getHand().get(handSize-1));
                           if(cardPlayed!= null){
                               msg += "You play a(n) " + cardPlayed.getRank() + " " + cardPlayed.getSuit() + "\n";
                               game.updateTurn();
                               break;
                           }
                       }

                       else{
                           msg += "You have a card you can play, pick that instead\n";
                           //Otherwise they are free to play a valid card if they already had it
                           break;
                       }

                   }
               }

                System.out.println("Num drawn cards: "+numDrawnCards);

               //If after 3 draws, the player can play a card, play it. Update the turn regardless
               if(numDrawnCards == 3){
                   if(game.canPlayFromHand(player)){
                       int handSize = player.getHand().size();
                       Card cardPlayed = game.playCard(player, player.getHand().get(handSize-1));
                       if(cardPlayed != null){
                           msg += "You play a(n) " + cardPlayed.getRank() + " " + cardPlayed.getSuit() + "\n";
                       }
                   }
                   System.out.println("I am updating the turn when a player draws 3 cards");
                   game.updateTurn();
               }


                response.setMessage(msg);

            }
            else{
                game.setTopCard(card);
                game.updateTurn();
                response.setMessage("yes");
            }



            response.setCards(stringifyCards(player.getHand()));

            response.setDirection(game.getDirection());
            response.setTurnNumber(Integer.toString(game.getTurn()));

        }
        return response;
    }

    @MessageMapping("/private-message")
    public ServerMessage receivePrivateMessage(@Payload ClientMessage message){
        ServerMessage response = new ServerMessage();

        Player player = null;
        for(int i = 0; i < game.getPlayers().size(); ++i){
            Player p = game.getPlayers().get(i);
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

//        System.out.println(response.toString());
        if(player == null){
            response.setId("1");

        }

        else{
            response.setCurrentPlayerTurn(player.getName());
            response.setId(Integer.toString(player.getId()));
        }

        simpMessagingTemplate.convertAndSendToUser(response.getName(), "/private", response); //user/Jola/private

        return response;
    }

}

