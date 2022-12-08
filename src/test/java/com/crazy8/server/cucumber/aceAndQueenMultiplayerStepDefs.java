package com.crazy8.server.cucumber;

import com.crazy8.game.Card;
import com.crazy8.game.Deck;
import com.crazy8.game.Defs;
import com.crazy8.game.Game;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@SpringBootTest
@DirtiesContext
public class aceAndQueenMultiplayerStepDefs {
    @Autowired
    private Game game;

    private static final ArrayList<WebDriver> webDrivers = new ArrayList<>();
    private static final String PORT_URL = "http://127.0.0.1:8080";
    private static final By NAME_TEXTBOX = By.id("name-textbox");
    private static final By JOIN_BUTTON = By.id("join-button");
    private static final By PLAY_CARD_BUTTON = By.id("play-card-button");
    private static final By CURRENT_TURN = By.id("current-turn");
    private static final By CURRENT_GAME_DIRECTION = By.id("current-game-direction");

    private static final By HAND = By.className("cardsList");

    private static final Card topCardOne = new Card(Defs.Rank.EIGHT, Defs.Suit.HEARTS);

    private static final ArrayList<Card> playerOneHand =
            new ArrayList<>(Arrays.asList(
                    //needed for rigging
                    new Card(Defs.Rank.THREE, Defs.Suit.CLUBS),
                    new Card(Defs.Rank.ACE, Defs.Suit.HEARTS),
                    new Card(Defs.Rank.QUEEN, Defs.Suit.CLUBS),
                    //not needed for rigging
                    new Card(Defs.Rank.TWO, Defs.Suit.CLUBS),
                    new Card(Defs.Rank.THREE, Defs.Suit.HEARTS)
            )

            );

    private static final ArrayList<Card> playerTwoHand =
            new ArrayList<>(Arrays.asList(
                    new Card(Defs.Rank.FOUR, Defs.Suit.CLUBS),
                    //not needed for rigging
                    new Card(Defs.Rank.ACE, Defs.Suit.SPADES),
                    new Card(Defs.Rank.TWO, Defs.Suit.HEARTS),
                    new Card(Defs.Rank.THREE, Defs.Suit.SPADES),
                    new Card(Defs.Rank.FOUR, Defs.Suit.SPADES)
            )

            );

    private static final ArrayList<Card> playerThreeHand =
            new ArrayList<>(Arrays.asList(
                    //needed for rigging
                    new Card(Defs.Rank.SEVEN, Defs.Suit.HEARTS),
                    new Card(Defs.Rank.FIVE, Defs.Suit.CLUBS),
                    //not needed for rigging
                    new Card(Defs.Rank.THREE, Defs.Suit.HEARTS),
                    new Card(Defs.Rank.TWO, Defs.Suit.DIAMONDS),
                    new Card(Defs.Rank.THREE, Defs.Suit.DIAMONDS)
            )

            );

    private static final ArrayList<Card> playerFourHand =
            new ArrayList<>(Arrays.asList(
                    new Card(Defs.Rank.SEVEN, Defs.Suit.HEARTS),
                    new Card(Defs.Rank.NINE, Defs.Suit.SPADES),
                    new Card(Defs.Rank.ACE, Defs.Suit.CLUBS),
                    new Card(Defs.Rank.THREE, Defs.Suit.CLUBS),
                    new Card(Defs.Rank.FOUR, Defs.Suit.CLUBS)
            )

            );

    public WebElement waitForDisplayed(WebDriver webDriver, By selector) {
        return new WebDriverWait(webDriver, Duration.ofSeconds(10)).until(visibilityOf(webDriver.findElement(selector)));
    }

}
