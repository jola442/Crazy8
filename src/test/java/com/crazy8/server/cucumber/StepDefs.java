package com.crazy8.server.cucumber;

import com.crazy8.game.Card;
import com.crazy8.game.Deck;
import com.crazy8.game.Defs;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@DirtiesContext
public class StepDefs {

    private static final ArrayList<WebDriver> webDrivers = new ArrayList<>();
    public static final String PORT_URL = "http://127.0.0.1:8080";
    public static final By NAME_TEXTBOX = By.id("name-textbox");
    public static final By JOIN_BUTTON = By.id("join-button");
    public static final By PLAY_CARD_BUTTON = By.id("play-card-button");
    public static final By CURRENT_TURN = By.id("current-turn");
    public static final By CURRENT_GAME_DIRECTION = By.id("current-game-direction");
    public static final By TOP_CARD = By.xpath("//body/div[@id='root']/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]");

    private static final By HAND = By.className("cardsList");
    private ArrayList<Card> playerOneHand;
    private ArrayList<Card> playerTwoHand;
    private ArrayList<Card> playerThreeHand;
    private ArrayList<Card> playerFourHand;

    @Autowired
    private Game game;

    public boolean hasChildren(WebElement webElement) {
        return webElement.findElements(By.xpath("./descendant-or-self::*")).size() > 1;
    }

    public static boolean hasCSSClass(WebElement element, String className) {
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

    @Given("{} is being tested")
    public void functionalityIsBeingTested(String testName) {
        playerOneHand = new ArrayList<>();
        playerTwoHand = new ArrayList<>();
        playerThreeHand = new ArrayList<>();
        playerFourHand = new ArrayList<>();
        if(testName.equalsIgnoreCase("ace and queens functionality")){
            playerOneHand.addAll(Arrays.asList(
                    //needed for rigging
                    new Card(Rank.THREE, Suit.CLUBS),
                    new Card(Rank.ACE, Suit.HEARTS),
                    new Card(Rank.QUEEN, Suit.CLUBS),
                    new Card(Rank.THREE, Suit.HEARTS),
                    //not needed for rigging
                    new Card(Rank.TWO, Suit.CLUBS)

            ));

            playerTwoHand.addAll(Arrays.asList(
                    new Card(Rank.FOUR, Suit.CLUBS),
                    new Card(Rank.TWO, Suit.HEARTS),
                    new Card(Rank.FOUR, Suit.HEARTS),
                    //not needed for rigging
                    new Card(Rank.ACE, Suit.SPADES),
                    new Card(Rank.THREE, Suit.SPADES)

            ));

            playerThreeHand.addAll(Arrays.asList(
                    //needed for rigging
                    new Card(Rank.SEVEN, Suit.HEARTS),
                    new Card(Rank.FIVE, Suit.CLUBS),
                    new Card(Rank.THREE, Suit.HEARTS),
                    new Card(Rank.FIVE, Suit.HEARTS),
                    //not needed for rigging
                    new Card(Rank.THREE, Suit.DIAMONDS)
            ));

            playerFourHand.addAll((Arrays.asList(
                    //needed for rigging
                    new Card(Rank.QUEEN, Suit.CLUBS),
                    new Card(Rank.ACE, Suit.HEARTS),
                    new Card(Rank.THREE, Suit.CLUBS),
                    //Not needed for rigging
                    new Card(Rank.SEVEN, Suit.HEARTS),
                    new Card(Rank.FOUR, Suit.CLUBS))
            ));
        }

        else if(testName.equalsIgnoreCase("card playability functionality")){
            playerOneHand.addAll(Arrays.asList(
                    //needed for rigging
                    new Card(Rank.KING, Suit.HEARTS),
                    new Card(Rank.SEVEN, Suit.CLUBS),
                    new Card(Rank.EIGHT, Suit.HEARTS),
                    new Card(Rank.FIVE, Suit.SPADES),
                    //not needed for rigging
                    new Card(Rank.QUEEN, Suit.CLUBS)
            ));

            playerTwoHand.addAll(Arrays.asList(
                    //not needed for rigging
                    new Card(Rank.FOUR, Suit.CLUBS),
                    new Card(Rank.TWO, Suit.HEARTS),
                    new Card(Rank.FOUR, Suit.HEARTS),
                    new Card(Rank.ACE, Suit.SPADES),
                    new Card(Rank.THREE, Suit.SPADES)

            ));

            playerThreeHand.addAll(Arrays.asList(
                    //not needed for rigging
                    new Card(Rank.SEVEN, Suit.HEARTS),
                    new Card(Rank.FIVE, Suit.CLUBS),
                    new Card(Rank.THREE, Suit.HEARTS),
                    new Card(Rank.FIVE, Suit.HEARTS),
                    new Card(Rank.THREE, Suit.DIAMONDS)
            ));

            playerFourHand.addAll((Arrays.asList(
                    //Not needed for rigging
                    new Card(Rank.QUEEN, Suit.CLUBS),
                    new Card(Rank.ACE, Suit.HEARTS),
                    new Card(Rank.THREE, Suit.CLUBS),
                    new Card(Rank.SEVEN, Suit.HEARTS),
                    new Card(Rank.FOUR, Suit.CLUBS))
            ));
        }

        game.resetState();
        ArrayList<Card> riggedCards = new ArrayList<>();
        riggedCards.addAll(playerOneHand);
        riggedCards.addAll(playerTwoHand);
        riggedCards.addAll(playerThreeHand);
        riggedCards.addAll(playerFourHand);
        Deck riggedDeck = new Deck();
        riggedDeck.setCards(riggedCards);
        game.setDeck(riggedDeck);
    }


    @When("the starting card is {}")
    public void theStartingCardIs(String startingCardString) {
        Rank cardRank = Rank.valueOf(startingCardString.split("-")[0]);
        Suit cardSuit = Suit.valueOf(startingCardString.split("-")[1]);
        Card startingCard = new Card(cardRank, cardSuit);
        game.setTopCard(startingCard);
    }



    @Given("all players are connected")
    public void allPlayersAreConnected() {
        WebDriverManager.chromedriver().setup();
        for (int i = 0; i < 4; ++i) {
            webDrivers.add(new ChromeDriver());
            WebDriver webDriver = webDrivers.get(i);
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            webDriver.get(PORT_URL);
            webDriver.manage().window().maximize();
            webDriver.findElement(NAME_TEXTBOX).sendKeys("Player " + Integer.toString(i + 1));
            webDriver.findElement(JOIN_BUTTON).click();
        }

//        webDrivers.get(3).manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
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

            assertTrue(hasCSSClass(topCard, cardString));
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
                assertTrue(hasCSSClass(topCard, cardString));
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
                    assertTrue(hasCSSClass(topCard, cardString));
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


    @io.cucumber.java.en.Then("the top card should be {}")
    public void theTopCardShouldBeCard(String cardString) {
        Defs.Rank cardRank = Defs.Rank.valueOf(cardString.split("-")[0]);
        Defs.Suit cardSuit = Defs.Suit.valueOf(cardString.split("-")[1]);
        WebElement topCardDiv = webDrivers.get(0).findElement(TOP_CARD);
        cardString = cardString.toLowerCase();
        webDrivers.get(0).findElement(By.className(cardString)).click();
        webDrivers.get(0).findElement(PLAY_CARD_BUTTON).click();
        assertTrue(hasCSSClass(topCardDiv, cardString));
    }

}