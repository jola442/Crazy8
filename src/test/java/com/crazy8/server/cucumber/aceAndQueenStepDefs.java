package com.crazy8.server.cucumber;

import com.crazy8.game.Card;
import com.crazy8.game.Game;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.crazy8.game.Defs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
@DirtiesContext
public class aceAndQueenStepDefs {

    private static final ArrayList<WebDriver> webDrivers = new ArrayList<>();
    private static final String PORT_URL = "http://127.0.0.1:8080";
    private static final By NAME_TEXTBOX = By.id("name-textbox");
    private static final By JOIN_BUTTON = By.id("join-button");
    private static final By PLAY_CARD_BUTTON = By.id("play-card-button");
    private static final By CURRENT_TURN = By.id("current-turn");
    private static final By CURRENT_GAME_DIRECTION = By.id("current-game-direction");

    private static final By HAND = By.className("cardsList");

    private static final Card topCardOne = new Card(Rank.EIGHT, Suit.CLUBS);

//    private static final Card topCardTwo = new Card(Rank.EIGHT, Suit.HEARTS);
    private static final ArrayList<Card> playerOneHand =
            new ArrayList<>(Arrays.asList(
                    //needed for rigging
                    new Card(Rank.THREE, Suit.CLUBS),
                    new Card(Rank.ACE, Suit.HEARTS),
                    new Card(Rank.QUEEN, Suit.CLUBS),
                    //not needed for rigging
                    new Card(Rank.TWO, Suit.CLUBS),
                    new Card(Rank.THREE, Suit.HEARTS)
            )

            );

    private static final ArrayList<Card> playerTwoHand =
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.FOUR, Suit.CLUBS),
                    //not needed for rigging
                    new Card(Rank.ACE, Suit.SPADES),
                    new Card(Rank.TWO, Suit.SPADES),
                    new Card(Rank.THREE, Suit.SPADES),
                    new Card(Rank.FOUR, Suit.SPADES)
            )

            );

    private static final ArrayList<Card> playerThreeHand =
            new ArrayList<>(Arrays.asList(
                    //needed for rigging
                    new Card(Rank.SEVEN, Suit.HEARTS),
                    new Card(Rank.FIVE, Suit.CLUBS),
                    //not needed for rigging
                    new Card(Rank.ACE, Suit.DIAMONDS),
                    new Card(Rank.TWO, Suit.DIAMONDS),
                    new Card(Rank.THREE, Suit.DIAMONDS)
            )

            );

    private static final ArrayList<Card> playerFourHand =
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.QUEEN, Suit.CLUBS),
                    new Card(Rank.NINE, Suit.SPADES),
                    new Card(Rank.ACE, Suit.CLUBS),
                    new Card(Rank.THREE, Suit.CLUBS),
                    new Card(Rank.FOUR, Suit.CLUBS)
            )

            );

    @Autowired
    private Game game;

}
