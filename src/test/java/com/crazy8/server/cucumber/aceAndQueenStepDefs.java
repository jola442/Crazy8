package com.crazy8.server.cucumber;

import com.crazy8.game.Card;
import com.crazy8.game.Deck;
import com.crazy8.game.Game;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
                    new Card(Rank.THREE, Suit.HEARTS),
                    //not needed for rigging
                    new Card(Rank.TWO, Suit.CLUBS)

            )

            );

    private static final ArrayList<Card> playerTwoHand =
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.FOUR, Suit.CLUBS),
                    new Card(Rank.TWO, Suit.HEARTS),
                    new Card(Rank.FOUR, Suit.HEARTS),
                    //not needed for rigging
                    new Card(Rank.ACE, Suit.SPADES),
                    new Card(Rank.THREE, Suit.SPADES)

            )

            );

    private static final ArrayList<Card> playerThreeHand =
            new ArrayList<>(Arrays.asList(
                    //needed for rigging
                    new Card(Rank.SEVEN, Suit.HEARTS),
                    new Card(Rank.FIVE, Suit.CLUBS),
                    new Card(Rank.THREE, Suit.HEARTS),
                    new Card(Rank.FIVE, Suit.HEARTS),
                    //not needed for rigging
                    new Card(Rank.THREE, Suit.DIAMONDS)
            )

            );

    private static final ArrayList<Card> playerFourHand =
            new ArrayList<>(Arrays.asList(
                    //needed for rigging
                    new Card(Rank.QUEEN, Suit.CLUBS),
                    new Card(Rank.ACE, Suit.HEARTS),
                    new Card(Rank.THREE, Suit.CLUBS),
                    //Not needed for rigging
                    new Card(Rank.SEVEN, Suit.HEARTS),
                    new Card(Rank.FOUR, Suit.CLUBS)
            )

            );

    @Autowired
    private Game game;

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


    @Given("all players are connected and the starting card is {}")
    public void allPlayersAreConnected(String startingCardString) throws InterruptedException {
        game.resetState();
        ArrayList<Card> riggedCards = new ArrayList<>();
        Rank cardRank = Rank.valueOf(startingCardString.split("-")[0]);
        Suit cardSuit = Suit.valueOf(startingCardString.split("-")[1]);
        Card startingCard = new Card(cardRank, cardSuit);
        System.out.println("Card i created in the test method: " + startingCard);
        game.setTopCard(startingCard);
        riggedCards.addAll(playerOneHand);
        riggedCards.addAll(playerTwoHand);
        riggedCards.addAll(playerThreeHand);
        riggedCards.addAll(playerFourHand);
        System.out.println("Rigged cards: " + riggedCards);
        Deck riggedDeck = new Deck();
        riggedDeck.setCards(riggedCards);
        game.setDeck(riggedDeck);
        WebDriverManager.chromedriver().setup();
        for (int i = 0; i < 4; ++i) {
            webDrivers.add(new ChromeDriver());
            WebDriver webDriver = webDrivers.get(i);
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            webDriver.get(PORT_URL);
            webDriver.manage().window().maximize();
            webDriver.findElement(NAME_TEXTBOX).sendKeys("Player " + Integer.toString(i + 1));
            webDriver.findElement(JOIN_BUTTON).click();
        }

        webDrivers.get(3).manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        assertEquals("LEFT", webDrivers.get(3).findElement(CURRENT_GAME_DIRECTION).getText());
    }


    @When("player {} plays {}")
    public void playerPlaysCard(int playerNum, String cardString) throws InterruptedException {
        if (playerNum == 1) {
            WebDriver webDriver = webDrivers.get(0);
            cardString = cardString.toLowerCase();
            webDriver.findElement(By.className(cardString)).click();
            WebElement playCardButton = webDriver.findElement(PLAY_CARD_BUTTON);
            playCardButton.click();
            WebElement topCard = webDriver.findElement(By.xpath("//body/div[@id='root']/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]"));
            System.out.println("TEST: Current top card " + topCard.getAttribute("innerHTML"));
            System.out.println(cardString);

            assertTrue(hasClass(topCard, cardString));
        } else {
            if (playerNum == 4) {
                ArrayList<String> playerCardSelections = new ArrayList<String>();
                if (game.getTopCard().equals(new Card(Rank.EIGHT, Suit.CLUBS))) {
                    playerCardSelections.addAll(Arrays.asList("3-clubs", "4-clubs", "5-clubs"));
                } else {
                    playerCardSelections.addAll(Arrays.asList("3-hearts", "4-hearts", "5-hearts"));
                }


                for (int i = 0; i < playerCardSelections.size(); ++i) {
                    WebDriver webDriver = webDrivers.get(i);
                    String playerCardSelection = playerCardSelections.get(i);
                    webDriver.findElement(By.className(playerCardSelection)).click();
                    WebElement playCardButton = webDriver.findElement(PLAY_CARD_BUTTON);
                    playCardButton.click();
                    System.out.println("TEST: PLAYER " + (i+1) + " playing " + playerCardSelection);
                }

                WebDriver webDriver = webDrivers.get(3);
                cardString = cardString.toLowerCase();

                System.out.println("TEST: PLAYER 4 playing " + cardString);
                webDriver.findElement(By.className(cardString)).click();
                WebElement playCardButton = webDriver.findElement(PLAY_CARD_BUTTON);
                playCardButton.click();

                WebElement topCard = webDriver.findElement(By.xpath("//body/div[@id='root']/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]"));

                System.out.println("TEST: Top card is currently: " + topCard.getAttribute("class"));
//                System.out.println("TEST: Checking if this was played: " + cardString);
//            Thread.sleep(5000);
                assertTrue(hasClass(topCard, cardString));
            } else {
                if (playerNum == 3) {
                    WebDriver webDriver = webDrivers.get(2);
                    cardString = cardString.toLowerCase();
                    webDriver.findElement(By.className(cardString)).click();
                    WebElement playCardButton = webDriver.findElement(PLAY_CARD_BUTTON);
                    playCardButton.click();
                    System.out.println("TEST: PLAYER " + 3 + " playing " + cardString);
                    WebElement topCard = webDriver.findElement(By.xpath("//body/div[@id='root']/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]"));

                    System.out.println("TEST: The top card is currently: " + topCard.getAttribute("class"));
                    System.out.println("TEST: Checking if this was played: " + cardString);
//            Thread.sleep(5000);
                    assertTrue(hasClass(topCard, cardString));
                }
            }

        }

    }

    @Then("player {} should play next")
    public void shouldPlayNext(int nextPlayer) throws InterruptedException {
        WebDriver webDriver = webDrivers.get(3);
//        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        Thread.sleep(5000);
        WebElement currentTurn = webDriver.findElement(CURRENT_TURN);
        assertEquals("Player " + Integer.toString(nextPlayer), currentTurn.getText());
    }

    @And("the game direction is now {}")
    public void theGameDirectionIsNow(Direction newDirection) {
        WebDriver webDriver = webDrivers.get(3);
//        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        WebElement currentGameDirection = webDriver.findElement(CURRENT_GAME_DIRECTION);
        assertEquals(newDirection.toString(), currentGameDirection.getText());
    }
}