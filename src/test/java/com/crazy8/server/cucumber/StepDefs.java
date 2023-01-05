package com.crazy8.server.cucumber;

import com.crazy8.game.Card;
import com.crazy8.game.Deck;
import com.crazy8.game.Game;
import com.crazy8.game.Player;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
public class StepDefs {

    private static final ArrayList<WebDriver> webDrivers = new ArrayList<>();
    public static final String PORT_URL = "http://127.0.0.1:8080";
    public static final By NAME_TEXTBOX = By.id("name-textbox");
    public static final By JOIN_BUTTON = By.id("join-button");
    public static final By PLAY_CARD_BUTTON = By.id("play-card-button");
    private static final By DRAW_CARD_BUTTON = By.id("draw-card-button");
    public static final By CURRENT_TURN = By.id("current-turn");
    public static final By CURRENT_GAME_DIRECTION = By.id("current-game-direction");
    public static final By TOP_CARD = By.xpath("//body/div[@id='root']/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]");
    private static final By SUIT_TEXTBOX = By.id("suit-textbox");
    private static final By HAND = By.className("cardsList");
    private static final By PLAYER_ONE_SCORE = By.id("player-1-score");
    private static final By PLAYER_TWO_SCORE = By.id("player-2-score");
    private static final By PLAYER_THREE_SCORE = By.id("player-3-score");
    private static final By PLAYER_FOUR_SCORE = By.id("player-4-score");

    @Autowired
    private Game game;

    public boolean hasChildren(WebElement webElement) {
        return webElement.findElements(By.xpath("./descendant-or-self::*")).size() > 1;
    }

    public static boolean hasCSSClass(WebElement element, String className) {
        return Arrays.asList(element.getAttribute("class").split(" ")).contains(className);
    }

    public static boolean checkAnnouncements(WebDriver webDriver, String announcement){
        String searchString = "//p[contains(text(),'" + announcement + "')]";
//        webDriver.findElement(By.xpath("//p[contains(text(),'You cannot play this card')]");
        return webDriver.findElements(By.xpath(searchString)).size() > 0;
    }

    public static boolean handContainsCard(WebDriver webDriver, String cardString){
        System.out.println("TEST: Finding " + cardString + webDriver.findElement(By.cssSelector(".cardsList ." + cardString)).getAttribute("class"));
        return webDriver.findElements(By.cssSelector(".cardsList ." + cardString)).size() > 0;
    }


    @After
    public void tearDown() {
        for (int i = 0; i < webDrivers.size(); ++i) {
            WebDriver webDriver = webDrivers.get(i);
            webDriver.quit();
        }
        webDrivers.clear();
    }

    @When("the top card is {}")
    public void theStartingCardIs(String startingCardString) {
        Rank cardRank = Rank.valueOf(startingCardString.split("-")[0]);
        Suit cardSuit = Suit.valueOf(startingCardString.split("-")[1]);
        Card startingCard = new Card(cardRank, cardSuit);
        game.setTopCard(startingCard);
    }

    @And("player {int} is to play first")
    public void playerIsToPlayFirst(int playerNum) {
        game.setTurn(playerNum);
    }


    @Given("all players are connected")
    public void allPlayersAreConnected() {
        WebDriverManager.chromedriver().setup();
        for (int i = 0; i < 4; ++i) {
            webDrivers.add(new ChromeDriver());
            WebDriver webDriver = webDrivers.get(i);
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
            webDriver.get(PORT_URL);
            webDriver.manage().window().maximize();
            webDriver.findElement(NAME_TEXTBOX).sendKeys("Player " + Integer.toString(i + 1));
            webDriver.findElement(JOIN_BUTTON).click();
        }

        assertEquals("RIGHT", webDrivers.get(3).findElement(CURRENT_GAME_DIRECTION).getText());
    }


    @When("player {int} plays {}")
    public void playerPlaysCard(int playerNum, String cardString){
        WebDriver webDriver = webDrivers.get(playerNum-1);
        cardString = cardString.toLowerCase();
        webDriver.findElement(By.className(cardString)).click();
        WebElement playCardButton = webDriver.findElement(PLAY_CARD_BUTTON);
        playCardButton.click();
    }

    @Then("player {} should play next")
    public void thenShouldPlayNext(int nextPlayer) throws InterruptedException {
        WebDriver webDriver = webDrivers.get(3);
        WebElement currentTurn = webDriver.findElement(CURRENT_TURN);
        assertEquals("Player " + Integer.toString(nextPlayer), currentTurn.getText());
    }

    @And("player {} is the next to play")
    public void andIsNextToPlay(int nextPlayer) throws InterruptedException {
        thenShouldPlayNext(nextPlayer);
    }

    @And("the game direction is now {}")
    public void theGameDirectionIsNow(Direction newDirection) {
        WebDriver webDriver = webDrivers.get(3);
        WebElement currentGameDirection = webDriver.findElement(CURRENT_GAME_DIRECTION);
        assertEquals(newDirection.toString(), currentGameDirection.getText());
    }


    @io.cucumber.java.en.Then("the top card should be {}")
    public void theTopCardShouldBeCard(String cardString) throws InterruptedException {
        WebElement topCardDiv = webDrivers.get(0).findElement(TOP_CARD);
        cardString = cardString.toLowerCase();
        assertTrue(hasCSSClass(topCardDiv, cardString));
    }

    @When("player {int} attempts to play {}")
    public void playerAttemptsToPlay(int playerNum, String cardString) {
        playerPlaysCard(playerNum, cardString);
    }

    @Then("the game should send player {} a message saying the card is invalid")
    public void theGameShouldSendAMessageSayingTheCardIsInvalid(int playerNum) {
        WebDriver webDriver = webDrivers.get(playerNum-1);
        boolean messageIsDisplayed = checkAnnouncements(webDriver, "You cannot play this card");
        assertTrue(messageIsDisplayed);
    }


    @Then("the game should prompt the player {} for a new suit")
    public void theGameShouldPromptThePlayerForANewSuit(int playerNum) {
        boolean promptIsDisplayed = webDrivers.get(playerNum-1).findElements(SUIT_TEXTBOX).size() > 0;
        assertTrue(promptIsDisplayed);
    }

    @And("player {int} draws {} and plays {}")
    public void andPlayerDrawsCardAndPlaysIt(int playerNum, String cardsList, String cardString) {
        String[] cardArray = cardsList.split(",");
        ArrayList<Card> riggedCards = new ArrayList<>();
        WebDriver webDriver = webDrivers.get(playerNum-1);

        for(int i = 0; i < cardArray.length; ++i){
            Rank rank = Rank.valueOf(cardArray[i].split("-")[0]);
            Suit suit = Suit.valueOf(cardArray[i].split("-")[1]);
            riggedCards.add(new Card(rank, suit));
        }

        Deck riggedDeck = new Deck();
        riggedDeck.setCards(riggedCards);
        game.setDeck(riggedDeck);

        //The player has to try and play an invalid card first and the game will draw cards on their behalf
        //Playing the first card in the player's hand
        webDriver.findElement(By.cssSelector(".cardsList .card")).click();
//        System.out.println("TEST: This should be the first card in the hand: " + webDriver.findElement(By.cssSelector(".cardsList .card")).getAttribute("class"));
        webDriver.findElement(PLAY_CARD_BUTTON).click();

        //assert the cards were drawn
        for(int i = 0; i < riggedCards.size(); ++i){
            //If only one card is going to be drawn, then it will be played, so it won't be in the player's hand
            if(riggedCards.size() == 1){
                break;
            }
            //The last card mentioned is always the one played, so it is not among the drawn cards
            if(i == riggedCards.size()-1){
                continue;
            }
            String rank = riggedCards.get(i).getRank().toString();
            String suit = riggedCards.get(i).getSuit().toString().toLowerCase();
            assertTrue(handContainsCard(webDriver, rank+"-"+suit));
        }
    }


    @And("player {int} draws {} and can't play")
    public void playerDrawsAndCantPlay(int playerNum, String cardsList) {
        String[] cardArray = cardsList.split(",");
        ArrayList<Card> riggedCards = new ArrayList<>();
        WebDriver webDriver = webDrivers.get(playerNum-1);

        for(int i = 0; i < cardArray.length; ++i){
            Rank rank = Rank.valueOf(cardArray[i].split("-")[0].strip());
            Suit suit = Suit.valueOf(cardArray[i].split("-")[1].strip());
            riggedCards.add(new Card(rank, suit));
        }

        Deck riggedDeck = new Deck();
        riggedDeck.setCards(riggedCards);
        game.setDeck(riggedDeck);

        //The player has to try and play an invalid card first and the game will draw cards on their behalf
        //Playing the first card in the player's hand
        webDriver.findElement(By.cssSelector(".cardsList .card")).click();
        webDriver.findElement(PLAY_CARD_BUTTON).click();

        //assert the cards were drawn
        for(int i = 0; i < riggedCards.size(); ++i){
            String rank = riggedCards.get(i).getRank().toString();
            String suit = riggedCards.get(i).getSuit().toString().toLowerCase();
            assertTrue(handContainsCard(webDriver, rank+"-"+suit));
        }

    }

    @And("player {int} chooses to draw {} and plays it")
    public void playerChoosesToDraw(int playerNum, String cardsList) {
        String[] cardsArray = cardsList.split(",");
        ArrayList<Card> riggedCards = new ArrayList<>();
        WebDriver webDriver = webDrivers.get(playerNum-1);

        for(int i = 0; i < cardsArray.length; ++i){
            Rank rank = Rank.valueOf(cardsArray[i].split("-")[0]);
            Suit suit = Suit.valueOf(cardsArray[i].split("-")[1]);
            Card card = new Card(rank, suit);
            riggedCards.add(card);
        }

        Deck riggedDeck = new Deck();
        riggedDeck.setCards(riggedCards);
        game.setDeck(riggedDeck);

        webDriver.findElement(DRAW_CARD_BUTTON).click();
    }


    @And("players 1,2,3 and 4 score {int}, {int}, {int} and {int} respectively for this round")
    public void theRoundIsOverWithPlayersScoring(int p1Score, int p2Score, int p3Score, int p4Score) throws InterruptedException {
        assertEquals(p1Score, Integer.parseInt(webDrivers.get(0).findElement(PLAYER_ONE_SCORE).getText()));
        assertEquals(p2Score, Integer.parseInt(webDrivers.get(1).findElement(PLAYER_TWO_SCORE).getText()));
        assertEquals(p3Score, Integer.parseInt(webDrivers.get(2).findElement(PLAYER_THREE_SCORE).getText()));
        assertEquals(p4Score, Integer.parseInt(webDrivers.get(3).findElement(PLAYER_FOUR_SCORE).getText()));
    }


    @And("player {int} does not have any more cards")
    public void playerDoesNotHaveAnyMoreCards(int playerNum) {
        WebDriver webDriver = webDrivers.get(playerNum-1);
        boolean hasCards = hasChildren(webDriver.findElement(HAND));
        assertFalse(hasCards);
    }

    @And("player {int} starts their turn with {}")
    public void playerStartsTheirTurnWith(int playerNum, String playerHand) {
        String[] cardArray = playerHand.split(",");
        ArrayList<Card> riggedCards = new ArrayList<>();

        for(int i = 0; i < cardArray.length; ++i){
            Rank rank = Rank.valueOf(cardArray[i].split("-")[0].strip());
            Suit suit = Suit.valueOf(cardArray[i].split("-")[1].strip());
            riggedCards.add(new Card(rank, suit));
        }

        game.getDeck().getCards().addAll(riggedCards);
        game.getNumPlayerInitialCards().set(playerNum-1, riggedCards.size());
//        switch(playerNum){
//            case(1):
//                game.setNumPlayerOneInitialCards(riggedCards.size());
//                break;
//            case(2):
//                game.setNumPlayerTwoInitialCards(riggedCards.size());
//                break;
//            case(3):
//                game.setNumPlayerThreeInitialCards(riggedCards.size());
//                break;
//            case(4):
//                game.setNumPlayerFourInitialCards(riggedCards.size());
//                break;
//        }

    }


//    @And("player {int} draws {} and {} and plays {}")
//    public void playerDrawsMultipleOf2CardsAndPlays(int playerNum, String cardsList, String card, String cardPlayed){
//        WebDriver webDriver = webDrivers.get(playerNum-1);
//
//    }

    @Given("no player has won the game yet")
    public void noPlayerHasWonYet() {
        game.resetState();
    }


    @Then("the winner of the round is player {int}")
    public void theWinnerOfTheRoundIsPlayer(int playerNum) {
        for(int i = 0; i < webDrivers.size(); ++i){
            WebDriver webDriver = webDrivers.get(i);
            Player p = game.getPlayers().get(playerNum-1);
            assertTrue(checkAnnouncements(webDriver, p.getName() + " has won round " + game.getRoundNum() + "!"));
        }
    }

    @And("player {int} avoids the two-card penalty by playing {}")
    public void playerAvoidsTheTwoCardPenaltyByPlaying(int playerNum, String cardsList) {
        WebDriver webDriver = webDrivers.get(playerNum-1);
        String[] cardsArr = cardsList.split(",");
        WebElement playCardButton = webDriver.findElement(PLAY_CARD_BUTTON);

        for(int i = 0; i < cardsArr.length; ++i){
            webDriver.findElement(By.className(cardsArr[i].toLowerCase().strip())).click();
        }
        playCardButton.click();
    }

    //TWO-CARD STEP DEFINITIONS
    @And("player {int} cannot play {int} cards from their hand")
    public void playerCannotPlayCardsFromTheirHand(int playerNum, int numCards) {
        //empty because the server controls this behaviour
    }

    @And("the cards at the top of the stockpile are {}")
    public void theCardsAtTheTopOfTheStockpileAre(String cardsList) {
        //add them in reverse order because the top of the deck is the last element in the deck arraylist
        String[] cardsArray = cardsList.strip().split(",");
        ArrayList<Card> riggedCards = new ArrayList<>();

        for(int i = cardsArray.length-1; i >= 0; --i){
            Rank rank = Rank.valueOf(cardsArray[i].strip().split("-")[0]);
            Suit suit = Suit.valueOf(cardsArray[i].strip().split("-")[1]);
            Card card = new Card(rank, suit);
            riggedCards.add(card);
        }

        game.getDeck().getCards().addAll(riggedCards);

    }

    @And("player {int} suffers the two-card penalty, drawing {int} cards from the top of the stockpile and {}")
    public void playerSuffersTheTwoCardPenalty(int playerNum, int numCardsDrawn, String playString) {
        //empty because the server controls this behaviour
    }

    @But("player {int}'s turn is not over so they draw {}")
    public void playerSTurnIsNotOverSoTheyDraw(int playerNum, String playString){
        //empty because the server controls this behaviour
    }
}