package com.crazy8.server;
import com.crazy8.game.Card;
import com.crazy8.game.Deck;
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

import static com.crazy8.game.Defs.*;


@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private Game game;

    public String stringifyCards(List<Card> cards){
        StringBuilder cardsString = new StringBuilder("[");
        for(int i = 0; i < cards.size(); ++i){
            Card card = cards.get(i);
            String cardSuit = card.getSuit().toString();
            String cardRank = card.getRank().toString();

            if(i < cards.size()-1){
                cardsString.append("{\"suit\": \"").append(cardSuit).append("\", \"rank\": \"").append(cardRank).append("\"},");
            }

            else{
                cardsString.append("{\"suit\": \"").append(cardSuit).append("\", \"rank\": \"").append(cardRank).append("\"}");
            }

        }
        cardsString.append("]");
        return cardsString.toString();
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
        switch (cardFromClient.charAt(suitIndex)) {
            case 'H' -> cardSuit = Suit.HEARTS;
            case 'S' -> cardSuit = Suit.SPADES;
            case 'D' -> cardSuit = Suit.DIAMONDS;
            case 'C' -> cardSuit = Suit.CLUBS;
        }
        
        return new Card(cardRank, cardSuit);

    }

    public ArrayList<Card> decodeCards(String cardsFromClient){
        String[] cardsListString = cardsFromClient.trim().split(",");
        ArrayList<Card> cards = new ArrayList<>();

        for(int i = 0; i < cardsListString.length; ++i){
            cards.add(decodeCard(cardsListString[i]));
        }

        return cards;
    }

    public void startNewRound(){
        game.setRoundNum(game.getRoundNum()+1);

        if(game.getRiggedDecks().size() >= game.getRoundNum()){
            Deck riggedDeck = game.getRiggedDecks().get(game.getRoundNum()-1);
            game.setDeck(riggedDeck);
        }

        else{
//            Deck newDeck = new Deck();
//            game.getDeck().getCards().clear();
//            game.getDeck().getCards().addAll(newDeck.getCards());
        }

        game.setDirection(Direction.RIGHT);

        if(game.getRoundNum() != 1){
            //The player that played after the first player in the previous round plays first in the new round
            game.setTurn(game.getRoundNum() % 4);
            if(game.getTurn() == 0){
                game.setTurn(4);
            }
        }



        if(game.getTopCard() == null){
            game.placeStartingCard();
        }

        for(int i = 0; i < game.getPlayers().size(); ++i){
            game.getPlayers().get(i).setRoundScore(0);
            game.getPlayers().get(i).getHand().clear();
        }

//        game.setDeck(new Deck());

        for(int i = 0; i < game.getNumPlayerInitialCards().size(); ++i){
            for(int j = 0; j < game.getNumPlayerInitialCards().get(i); ++j){
                game.drawCard(game.getPlayers().get(i));
            }
        }


    }

    public ServerMessage initResponse(ClientMessage message){
        ServerMessage response = new ServerMessage();
        response.setName(message.getName());
        response.setTurnNumber(Integer.toString(game.getTurn()));
        response.setDirection(game.getDirection());
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));
        response.setNumCardsDrawn(Integer.toString(game.getNumCardsDrawn()));
        response.setAction(message.getAction());
        response.setRoundNum(Integer.toString(game.getRoundNum()));
        return response;
    }

    private ServerMessage handleUserJoining(@Payload ClientMessage message){
        ServerMessage response = initResponse(message);

        //Creating the player model
        Player player = new Player(message.getName());
        game.getPlayers().add(player);
        player.setId(game.getPlayers().size());

        //Configuring the server response
        response.setId(Integer.toString(player.getId()));
        response.setMessage("Player " + (player.getId()) + " (" + player.getName() + ")" + " has joined." );
        response.setNumPlayers(Integer.toString(game.getPlayers().size()));

        if(game.getPlayers().size() == 4){
            System.out.println("CODE: Round number is now 1");
            startNewRound();
            response.setTurnNumber(Integer.toString(game.getTurn()));
        }
        return response;
    }

    private ServerMessage handleSendingTopCard(@Payload ClientMessage message){
        System.out.println("CODE: Handle sending top card was called");
        ServerMessage response = initResponse(message);

        ArrayList<Card> newTopCard = new ArrayList<>();
        StringBuilder msg = new StringBuilder();
        if(game.getTopCard() != null){
            if(message.getMessage().equalsIgnoreCase("hearts") ||
                    message.getMessage().equalsIgnoreCase("spades") ||
                    message.getMessage().equalsIgnoreCase("diamonds") ||
                    message.getMessage().equalsIgnoreCase("clubs")){
                game.setTopCard(new Card(Rank.EIGHT, Suit.valueOf(message.getMessage().toUpperCase())));
                msg.append("Player " + game.getTurn() + " declared " + message.getMessage() + " as the next suit");
                game.updateTurn();
                response = initResponse(message);
            }
            newTopCard.add(game.getTopCard());
            System.out.println("CODE: Game top card is not null");
            System.out.println("CODE: Round Number :" + game.getRoundNum());

            if(game.isEndOfRound()){
                game.updateRoundScores();
                game.updateGameScores();
                Player roundWinner = game.getRoundWinner();
                System.out.println("CODE: This player is the winner: " + roundWinner);
                StringBuilder scores = new StringBuilder();
                if(roundWinner != null){
                    msg.append(roundWinner.getName() + " has won round " + game.getRoundNum() + "!\n");
                    startNewRound();

                    //Updating the response with the new game values
                    response = initResponse(message);
                    game.setEndOfRound(false);
                    String[] playerScores = new String[game.getPlayers().size()];

                    for(int i = 0; i < playerScores.length; ++i){
                        playerScores[i] =  Integer.toString(game.getPlayers().get(i).getGameScore());
                        System.out.println("CODE: player hand after reset" + game.getPlayers().get(i).getHand());
                        if(i < playerScores.length-1){
                            scores.append(playerScores[i] + ",");
                        }

                        else{
                            scores.append(playerScores[i]);
                        }
                    }

                    response.setScores(scores.toString());
                    System.out.println("CODE: Scores being sent to the client: " + response.getScores());
                    response.setCards(stringifyCards(newTopCard));
                }

                Player gameWinner = game.getGameWinner();
                if(gameWinner != null){
                    msg.append(gameWinner.getName()).append(" has won the game!");
                }

                response.setMessage(msg.toString());

                return response;
            }


            if(game.getTopCard().getRank() == Rank.ACE){
                if(game.getDirection() == Direction.RIGHT){
                    int playerNumber = game.getTurn() == 1?4:game.getTurn()-1;
                    msg.append("Player ").append(playerNumber).append(" changed the direction from ").append(Direction.LEFT).append(" to ").append(Direction.RIGHT).append(" by playing an ACE");
                }

                else{
                    int playerNumber = game.getTurn() == 4?1:game.getTurn()+1;
                    msg.append("Player ").append(playerNumber).append(" changed the direction from ").append(Direction.RIGHT).append(" to ").append(Direction.LEFT).append(" by playing an ACE");
                }

            }

            else if(game.getTopCard().getRank() == Rank.QUEEN){
                int firstPlayer = 0;
                int secondPlayer = 0;

                if(game.getDirection() == Direction.RIGHT){
                    if(game.getTurn() == 4 || game.getTurn() == 3){
                        firstPlayer = game.getTurn()-2;
                        secondPlayer = game.getTurn()-1;
                    }

                    else if(game.getTurn() == 2){
                        firstPlayer = game.getTurn()+2;
                        secondPlayer = game.getTurn()-1;
                    }

                    else{
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

                msg.append("Player ").append(firstPlayer).append(" played a QUEEN causing Player ").append(secondPlayer).append(" to miss their turn");
                System.out.println("CODE: " + msg);

            }

            response.setMessage(msg.toString());
            System.out.println("CODE: Setting the top card to " + game.getTopCard());
            newTopCard.add(game.getTopCard());
        }



        response.setCards(stringifyCards(newTopCard));


        return response;
    }

    private ServerMessage handleSendingStartingTopCard(@Payload ClientMessage message){
        ServerMessage response = initResponse(message);

        ArrayList<Card> newTopCard = new ArrayList<>();
        newTopCard.add(game.getTopCard());
        response.setCards(stringifyCards(newTopCard));
        System.out.println("CODE: Sending the starting top card to all players " + newTopCard);

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
                response = initResponse(message);
                response.setMessage(message.getMessage());
            }

            else if(message.getMessage().equalsIgnoreCase("starting top card")){
                response = handleSendingStartingTopCard(message);
            }

            else{
                response = handleSendingTopCard(message);
            }

        }

        if(game.getTurn() >= 1 && game.getTurn()-1 <= game.getPlayers().size()-1){
            response.setCurrentPlayerTurn(game.getPlayers().get(game.getTurn()-1).getName());
        }

        return response;
    }

    public ServerMessage handleSendingUserID(@Payload ClientMessage message, Player player){
        ServerMessage response = initResponse(message);
        if(player == null){
            response.setId(Integer.toString(game.getPlayers().size()+1));
        }

        else{
            response.setId(Integer.toString(player.getId()));
        }

        return response;
    }

    private  ServerMessage handleSendingStartingCards(@Payload ClientMessage message, Player player) {
        ServerMessage response = initResponse(message);

        System.out.println("CODE: Sending starting cards to " + player.getName() + ": " + player.getHand());
        response.setCards(stringifyCards(player.getHand()));
        response.setMessage(message.getMessage());
        return response;
    }

    private ServerMessage handleSendingDrawnCard(@Payload ClientMessage message, Player player) {
        ServerMessage response = initResponse(message);

        Card drawnCard = game.drawCard(player);
        if (drawnCard != null) {
            game.setNumCardsDrawn(game.getNumCardsDrawn()+1);
            System.out.println("CODE:" + drawnCard + " is drawn");
            if(game.playCard(player,drawnCard) != null){
                if(game.getTopCard().getRank() == Rank.EIGHT){
                    response.setSelectSuit("true");
                    response.setMessage("played");
                    return response;
                }
                System.out.println("CODE: " + drawnCard + " can be played");
                response.setMessage("played");
                game.updateTurn();
                return response;
            }
            ArrayList<Card> cardsToSend = new ArrayList<>();
            cardsToSend.add(drawnCard);
            response.setMessage("kept");
            response.setCards(stringifyCards(cardsToSend));
            response.setNumCardsDrawn(Integer.toString(game.getNumCardsDrawn()));
        }

        else{
            response.setMessage("The deck is empty");
        }

        return response;
    }


    private ServerMessage handleTwoCardAsTopCard(@Payload ClientMessage message, Player player) {
        ServerMessage response = initResponse(message);

        int numCardsToPlay = TWO_CARD_PENALTY * game.getNumStackedTwoCards();
        boolean playerCanPlay = game.canPlayFromHand(player, numCardsToPlay);
        boolean playerHasATwo = player.getHand().contains(new Card(Rank.TWO, Suit.SPADES))||
                player.getHand().contains(new Card (Rank.TWO, Suit.DIAMONDS))||
                player.getHand().contains(new Card (Rank.TWO, Suit.DIAMONDS))||
                player.getHand().contains(new Card (Rank.TWO, Suit.DIAMONDS));
        StringBuilder msg = new StringBuilder(game.getNumStackedTwoCards() + " player(s) played a " + game.getTopCard() + "\n So you have to play " + numCardsToPlay + " cards or draw " + numCardsToPlay + " cards\n");
        if(playerCanPlay || playerHasATwo){
            System.out.println("CODE: " + player.getName() + " can play");
            response.setAction(Action.PLAY);
            msg.append("You have card(s) you can play to avoid the two-card penalty");
        }

        else{
            response.setAction(Action.DRAW);
            msg.append("You can't play up to ").append(numCardsToPlay).append(" cards \n So ");
            System.out.println("CODE: " + player.getName() + " has to draw " + numCardsToPlay + " cards");
            ArrayList<Card> drawnCards = new ArrayList<>();

            for(int i = 0; i < numCardsToPlay; i++){
                Card card = game.drawCard(player);
                drawnCards.add(card);
                msg.append("you draw a(n) ").append(card.getRank()).append(" ").append(card.getSuit()).append("\n");

            }

            System.out.println("CODE: Drawn cards: " + drawnCards);

            //Play the first playable card drawn from the 2-card penalty
            if(game.canPlayFromHand(player)){
                for(int i = 0; i < player.getHand().size(); ++i){
                    Card card = player.getHand().get(i);
                    if(game.playCard(player, card) != null){
                        if(game.getTopCard().getRank() == Rank.EIGHT){
                            msg.append("you play a(n) ").append(card.getRank()).append(" ").append(card.getSuit()).append("\n");
                            response.setSelectSuit("true");
                            response.setMessage(msg.toString());
                            response.setAction(Action.PLAY);
                            response.setTurnNumber(Integer.toString(game.getTurn()));
                            response.setCards(stringifyCards(player.getHand()));
                            return response;
                        }
                        response.setAction(Action.PLAY);
                        msg.append("you play a(n) ").append(card.getRank()).append(" ").append(card.getSuit()).append("\n");
                        game.updateTurn();
                        response.setTurnNumber(Integer.toString(game.getTurn()));
                        response.setCards(stringifyCards(player.getHand()));
                        break;
                    }
                }
            }

            else{
                response.setCards(stringifyCards(drawnCards));
            }

        }
        response.setMessage(msg.toString());
        response.setNumStackedTwoCards(Integer.toString(game.getNumStackedTwoCards()));

        return response;
}

    private ServerMessage handlePlayingACard(@Payload ClientMessage message, Player player){
        ServerMessage response = initResponse(message);


        if(message.getMessage().equalsIgnoreCase("")){
            response.setMessage("no");
        }

        else{
            ArrayList<Card> cards = decodeCards(message.getMessage());
            //If the player is only requesting to play 1 card (always the case except when a player can avoid the 2-card penalty)
            if(cards.size() == 1){

                Card card =  decodeCard(message.getMessage());

                if(game.getTopCard().getRank() == Rank.TWO && card.getRank() != Rank.TWO){
                    response.setMessage("If you are playing only 1 card, it must be a 2 \n You have card(s) you can play to avoid the two-card penalty");
                    response.setCards(stringifyCards(player.getHand()));
                    response.setDirection(game.getDirection());
                    response.setTurnNumber(Integer.toString(game.getTurn()));
                    return response;
                }

                System.out.println("CODE: Player " + game.getTurn() + " plays a " + card);


                //If the selected card cannot be played
                if(game.playCard(player, card) == null){
                    StringBuilder msg = new StringBuilder("You cannot play this card (" + card.getRank() + " " + card.getSuit() + ")\n");

                    int numDrawnCards = 0;


                    //Draw up to 3 cards and play
                    for(int i = 0; i < MAX_NUM_DRAWS_PER_TURN; ++i){
                        //If the player cannot play any card from their hand
                        if(!game.canPlayFromHand(player)){
                            Card newCard = game.drawCard(player);
                            //Check if the player successfully draws a card
                            if(newCard != null){
                                msg.append("You cannot play any other card from your hand \n So you draw a(n) ").append(newCard.getRank().toString()).append(" ").append(newCard.getSuit()).append("\n");
                                numDrawnCards++;
                            }

                            else{
                                msg.append("There are no cards left to draw\n");
                            }

                        }

                        //If the player can play a card from their hand
                        else{
                            //If they can play because they drew a card, they must play the card
                            if(numDrawnCards >= 1){
                                int handSize = player.getHand().size();
                                Card cardPlayed = game.playCard(player, player.getHand().get(handSize-1));
                                if(cardPlayed!= null){
                                    msg.append("You play a(n) ").append(cardPlayed.getRank()).append(" ").append(cardPlayed.getSuit()).append("\n");
                                    if(game.getTopCard().getRank() == Rank.EIGHT){
                                        response.setSelectSuit("true");
                                        response.setMessage(msg.toString());
                                        response.setCards(stringifyCards(player.getHand()));
                                        response.setDirection(game.getDirection());
                                        response.setTurnNumber(Integer.toString(game.getTurn()));
                                        return response;
                                    }
                                    game.updateTurn();
                                    break;
                                }
                            }

                            else{
                                msg.append("You have a card you can play, pick that instead\n");
                                //Otherwise they are free to play a valid card if they already had it
                                break;
                            }

                        }
                    }

                    System.out.println("CODE: Num drawn cards: "+numDrawnCards);

                    //If after 3 draws, the player can play a card, play it. Update the turn regardless
                    if(numDrawnCards == 3){
                        if(game.canPlayFromHand(player)){
                            int handSize = player.getHand().size();
                            Card cardPlayed = game.playCard(player, player.getHand().get(handSize-1));
                            if(cardPlayed != null){
                                msg.append("You play a(n) ").append(cardPlayed.getRank()).append(" ").append(cardPlayed.getSuit()).append("\n");
                                //ask the player for a suit if the last card they drew was an 8
                                if(game.getTopCard().getRank() == Rank.EIGHT){
                                    response.setSelectSuit("true");
                                    response.setMessage(msg.toString());
                                    return response;
                                }
                            }
                        }

                        game.updateTurn();
                    }


                    response.setMessage(msg.toString());

                }
                else{
                    if(game.getTopCard().getRank() == Rank.EIGHT){
                        response.setSelectSuit("true");
                    }

                    else{
                        game.updateTurn();
                    }

                    response.setMessage("yes");
                    if(player.getHand().size() == 0){
                        game.setEndOfRound(true);
                        System.out.println("CODE: " + player.getName() + " has no more cards");

                    }
                    System.out.println("CODE: Played a " + card);
                }

            }

            //The UI only allows users to send multiple cards if the top card is a 2
            else{
                Card currentTopCard = game.getTopCard();
                //Checking if the set of cards can be played in the specified order
                for(int i = 0; i < cards.size(); ++i){
                    Card currentCard = cards.get(i);
                    //if the current card can't be played on the current top card
                    if(currentCard.getRank() != currentTopCard.getRank() && currentCard.getSuit() != currentTopCard.getSuit() && currentCard.getRank() != Rank.EIGHT){
                        response.setMessage("You can't play the cards in that order. Try a different one.");
                        response.setDirection(game.getDirection());
                        response.setTurnNumber(Integer.toString(game.getTurn()));
                        return response;
                    }

                    else{
                        currentTopCard = currentCard;
                    }
                }

                //Playing the cards
                for(int i = 0; i < cards.size(); ++i){
                    if(game.playCard(player, cards.get(i)) != null){
                        if(i == cards.size()-1){
                            if(game.getTopCard().getRank() == Rank.EIGHT){
                                response.setSelectSuit("true");
                            }
                        }
                        System.out.println("CODE: Successfully played " + cards.get(i));
                    }

                    else{
                        System.out.println("CODE: Unable to play " + cards.get(i));
                    }
                }

                if(player.getHand().size() == 0){
                    game.updateTurn();
                    game.setEndOfRound(true);
                    response.setSelectSuit("false");
                    System.out.println("CODE: " + player.getName() + " has no more cards");
//                    for(int i = 0; i < game.getPlayers().size(); ++i){
//                        Player p = game.getPlayers().get(i);
//                        int score = game.calculateScore(p.getHand());
//                        System.out.println("CODE: " + p.getName() + "'s score is now");
//                        p.setRoundScore(score);
//                    }

                }


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
//                System.out.println("CODE: STARTING CARDS WAS CALLED");
                response = handleSendingStartingCards(message, player);
            }

            else if(message.getMessage().equalsIgnoreCase("2 card")){
                response = handleTwoCardAsTopCard(message, player);
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

