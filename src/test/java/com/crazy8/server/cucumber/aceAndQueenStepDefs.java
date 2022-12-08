package com.crazy8.server.cucumber;

import com.crazy8.game.Card;
import com.crazy8.game.Deck;
import com.crazy8.game.Game;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.crazy8.game.Defs.*;
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

    public WebElement waitForDisplayed(WebDriver webDriver, By selector) {
        return new WebDriverWait(webDriver, Duration.ofSeconds(10)).until(visibilityOf(webDriver.findElement(selector)));
    }

    public boolean hasChildren(WebElement webElement) {
        return webElement.findElements(By.xpath("./descendant-or-self::*")).size() > 1;
    }

    public boolean hasClass(WebElement element, String className) {
        return Arrays.asList(element.getAttribute("class").split(" ")).contains(className);
    }

    @After
    public void tearDown() {
        for (int i = 0; i < webDrivers.size(); ++i) {
            WebDriver webDriver = webDrivers.get(i);
            webDriver.quit();
        }
        webDrivers.clear();
        game.resetState();
    }


    @Given("all players are connected")
    public void allPlayersAreConnected() throws InterruptedException {
        game.resetState();
        ArrayList<Card> riggedCards = new ArrayList<>();
        game.setTopCard(topCardOne);
        riggedCards.addAll(playerOneHand);
        riggedCards.addAll(playerTwoHand);
        riggedCards.addAll(playerThreeHand);
        riggedCards.addAll(playerFourHand);
        System.out.println("Rigged cards: " + riggedCards);
        Deck riggedDeck = new Deck();
        riggedDeck.setCards(riggedCards);
        game.setDeck(riggedDeck);
        WebDriverManager.chromedriver().setup();
        for(int i = 0; i < 4; ++i) {
            webDrivers.add(new ChromeDriver());
            WebDriver webDriver = webDrivers.get(i);
            webDriver.get(PORT_URL);
            webDriver.manage().window().maximize();
            waitForDisplayed(webDriver, NAME_TEXTBOX);
            webDriver.findElement(NAME_TEXTBOX).sendKeys("Player " + Integer.toString(i + 1));
            waitForDisplayed(webDriver, JOIN_BUTTON);
            webDriver.findElement(JOIN_BUTTON).click();
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

        }



        for(int i = 0; i < 4; ++i){
            webDrivers.get(i).manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
//            new WebDriverWait(webDrivers.get(i), Duration.ofSeconds(30)).until(elementToBeClickable(webDrivers.get(3).findElement(By.cssSelector("ul.cardsList.li.card"))));
        }
        assertEquals("LEFT", webDrivers.get(3).findElement(CURRENT_GAME_DIRECTION).getText());
    }


}
