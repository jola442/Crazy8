import { useEffect, useState, useRef} from 'react'
import DOMPurify from 'dompurify';
import Cards from '../Cards';
import Card from "../Card"
import {over} from "stompjs"
import SockJS from "sockjs-client"
import "./index.css"
import {v4 as uuidv4} from "uuid";

let stompClient = null;
const GAME_SESSION_STORAGE_KEY = "crazy8.game"
const PLAYER_SESSION_STORAGE_KEY = "crazy8.player"
const ANNOUNCEMENTS_STORAGE_KEY = "crazy8.announcement"
const HAND_STORAGE_KEY = "crazy8.hand"
const TWO_CARD_PENALTY = 2;

function PlayRoom() {
    const [user, setUser] = useState({
        name:"",
        connected: false,
    })

    const [hand, setHand] = useState([])
    const [game, setGame] = useState({
        topCard: null,
        turn: "1",
        direction: "RIGHT",
        scores:["0","0","0","0"],
        numStackedTwoCards:0
    })

    const [announcements, setAnnouncements] = useState([]);
    const [playOrder, setPlayOrder] = useState([]);
    const suitInput = useRef();

    useEffect(() => {
    console.log("a re-render happened");
      const storedGame = JSON.parse(sessionStorage.getItem(GAME_SESSION_STORAGE_KEY));
      const storedPlayer = JSON.parse(sessionStorage.getItem(PLAYER_SESSION_STORAGE_KEY));
      const storedAnnouncements = JSON.parse(sessionStorage.getItem(ANNOUNCEMENTS_STORAGE_KEY));
      const storedHand = JSON.parse(sessionStorage.getItem(HAND_STORAGE_KEY));

      if(storedGame){
        setGame(storedGame)
      }

      if(storedPlayer){
        setUser(storedPlayer);
      }

      if(storedAnnouncements){
        setAnnouncements(storedAnnouncements);
      }

      if(storedHand){
        setHand(storedHand)
      }

    }, [])

    useEffect(() => {
        sessionStorage.setItem(GAME_SESSION_STORAGE_KEY, JSON.stringify(game));
      }, [game])

      useEffect(() => {
        sessionStorage.setItem(PLAYER_SESSION_STORAGE_KEY, JSON.stringify(user));
      }, [user])

      useEffect(() => {
        sessionStorage.setItem(ANNOUNCEMENTS_STORAGE_KEY, JSON.stringify(announcements));
      }, [announcements])

      useEffect(() => {
        sessionStorage.setItem(HAND_STORAGE_KEY, JSON.stringify(hand));
      }, [hand])

    //   useEffect( () => {
    //     console.log("Current user", user);
    //   }, [user])


      useEffect( () => {
        console.log("Current hand", hand);
      }, [hand])

    //   useEffect( () => {
    //     console.log("Current announcements", announcements);
    //   }, [announcements])


    const handleUsername=(event)=>{
        const {value}=event.target;
        setUser({...user,"name": value});
    }

    function handlePlayOrder(card){
        let cardIndex = playOrder.indexOf(card);
        let newPlayOrder = [];

        //only add the card if it's currently selected but not shown
        if(card.selected && cardIndex == -1){
            setPlayOrder( (oldPlayOrder) => ([...oldPlayOrder, card]))
        }

        //only remove the card it is shown but not currently selected
        else if(cardIndex > -1){
            setPlayOrder( (oldPlayOrder) => {
                newPlayOrder = [...oldPlayOrder]
                newPlayOrder.splice(cardIndex, 1);
                return newPlayOrder;
            })
        }

    }



    function toggleSelectedCard(id){
        const newHand = [...hand];
        const clickedCard = newHand.find( card => card.id === id);
        const selectedCards = newHand.filter( card => card.selected);
        console.log("top card: " + game.topCard.rank, game.topCard.suit);
        console.log("clicked card: " + clickedCard.rank, clickedCard.suit, clickedCard.selected);
        console.log("Selected cards");
        selectedCards.forEach( (card) => {console.log(card)});
        console.log("Hand");
        hand.forEach( (card) => {console.log(card)});

        //if there are no selected cards
        if(selectedCards.length === 0){
            if(clickedCard){
                console.log("Entered branch 1");
                clickedCard.selected = !clickedCard.selected;
//                if(clickedCard.selected){
//                    setSelectedCards( (oldSelectedCards) => ([...oldSelectedCards, clickedCard]))
//                }
//
//                else{
//                    setSelectedCards( (oldSelectedCards) => {
//                        let newSelectedCards = [...oldSelectedCards]
//                        for(let i = newSelectedCards.length-1; i >= 0; --i){
//                            if((newSelectedCards[i].rank === clickedCard[i].rank)
//                             && (newSelectedCards[i].suit === clickedCard[i].suit
//                             && (newSelectedCards[i].selected === clickedCard[i].selected))){
//                                newSelectedCards.splice(i, 1);
//                             }
//                        }
//                        return newSelectedCards;
//                    })
//                }
            }
        }

        //only one card is selected at a time unless a 2 is the top card
        else if(selectedCards.length >= 1){
            if(selectedCards.length == 1){
                if(game.topCard.rank !== "2"){
                    if(clickedCard.id === selectedCards[0].id){
                        console.log("Entered branch 2");
                        clickedCard.selected = !clickedCard.selected;
                    }
                }
            }

            if(game.topCard.rank === "2"){
              console.log("Entered branch 3");
                //if the player hasn't clicked as many cards as they need to play
                //allow them to select the card
                if(!clickedCard.selected){
                  console.log("Entered branch 4");
                    if(selectedCards.length < Number(game.numStackedTwoCards) * TWO_CARD_PENALTY){
                      console.log("Entered branch 5");
                        console.log("selectedCards.length < game.numStackedTwoCards * TWO_CARD_PENALTY")
                        clickedCard.selected = true;
                    }
                }

                //allow the player to unselect the card
                else{
                    clickedCard.selected = false;
                }
            }
        }
        setHand(newHand);
        handlePlayOrder(clickedCard);
    }



    function getSelectedCard(){
        let selectedCard = hand.filter( card => card.selected);
        if(selectedCard.length > 0){
            return selectedCard[0];
        }

        else{
            return "";
        }

    }

    function encodeCard(card){
        let rank = ""

        switch(card.rank){
            case("ace"):
                rank = "1"
                break;
            case("jack"):
                rank = "11"
                break;
            case("queen"):
                rank = "12"
                break;
            case("king"):
                rank = "13"
                break;
            default:
                rank = card.rank;
        }
        if(card){
            return rank + card.suit[0].toUpperCase();
        }

        else{
            return "";
        }

    }

//    function encodeCards(){
//        playOrder.forEach( (card) => (encodeCard(card));
//    }

  // Networking functions
    const registerUser = () => {
        let Sock = new SockJS("http://localhost:8080/ws")
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    }

    const onConnected = async() =>{
        // saveUsername();
        setUser( (oldUser) => ({...oldUser, ...{connected:true}}));
        await stompClient.subscribe("/playroom/public", onPublicMessageReceived);
        console.log("current username:", user.name);
        await stompClient.subscribe('/player/'+user.name+'/private', onPrivateMessageReceived);
        userJoins();
    }

    const onError = (error) => {
        console.log(error);
    }
    const userJoins = async() =>{
        let message = {
            name: user.name,
            action: 'JOIN'
        };
        await stompClient.send("/app/message", {}, JSON.stringify(message));
        requestID();
    }

    async function requestID(){
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: "",
                action: 'JOIN',
            };
            await stompClient.send("/app/private-message", {}, JSON.stringify(messageToSend));
        }
    }

    function requestStartingCards(){
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: "starting cards",
                action: 'DRAW',
            };
            stompClient.send("/app/private-message", {}, JSON.stringify(messageToSend));

        }
    }

    function sendTwoCardMessage(){
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: "2 card",
                action: 'DRAW',
            };
            stompClient.send("/app/private-message", {}, JSON.stringify(messageToSend));

        }
    }

    function requestPublicInformation(message){
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: message,
                action:"UPDATE"
            };
            stompClient.send("/app/message", {}, JSON.stringify(messageToSend));

        }
    }

    function sendSuit(){
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: suitInput.current.value,
                action:"UPDATE"
            };
            suitInput.current.value = "";
            stompClient.send("/app/message", {}, JSON.stringify(messageToSend));

        }
    }

    const onPublicMessageReceived = (payload) => {
        let payloadData = JSON.parse(payload.body);
        console.log("Payload Data: " + payloadData)
        switch(payloadData.action){
            case "JOIN":
                setAnnouncements((oldAnnouncements) => ([...oldAnnouncements, {id:uuidv4(), message:payloadData.message}]))
                setGame((oldGame)=>({...oldGame, ...{turn:payloadData.turnNumber}}));
                if(payloadData.numPlayers === "4"){
                    requestStartingCards();
                    requestPublicInformation("starting top card");
                }
                break;

            case "UPDATE":
                if(payloadData.message.toLowerCase().includes("won")){
                    let scores = payloadData.scores.split(",");
                    let payloadCards = JSON.parse(payloadData.cards);
                    const newTopCard = payloadCards[0];
                    newTopCard.id = uuidv4();
                    newTopCard.selected = false;
                    newTopCard.front = true;
                    setGame((oldGame)=>({...oldGame, ...{turn:payloadData.turnNumber, topCard:newTopCard, direction:payloadData.direction, scores:scores}}));
                    setAnnouncements([{id:uuidv4(), message:payloadData.message}]);

                }
                if(payloadData.message.toLowerCase() === "turn"){
                    setGame((oldGame)=>({...oldGame, ...{turn:payloadData.turnNumber}}));
                }

                else{
                    let payloadCards = JSON.parse(payloadData.cards);
                    const newTopCard = payloadCards[0];
                    newTopCard.id = uuidv4();
                    newTopCard.selected = false;
                    newTopCard.front = true;
                    setGame((oldGame)=>({...oldGame, ...{turn:payloadData.turnNumber, topCard:newTopCard, direction:payloadData.direction}}));


                    //The server sends a message unless the top card is a starting top card
                    if(payloadData.message !== "" && payloadData.message.toLowerCase() !== "starting top card"){
                        console.log("Not equal to starting card or '' ran with this Message:",payloadData.message);
                        setAnnouncements([{id:uuidv4(), message:payloadData.message}]);
                    }

                    else{
                        console.log("Not equal to starting card or '' DID NOT run with this Message:",payloadData.message);

                        if(payloadData.currentPlayerTurn === user.name){
                            console.log("New top card rank: ", newTopCard.rank);
                            if(newTopCard.rank === "2"){
                                console.log("Sent the two card message");
                                sendTwoCardMessage();
                            }
                        }
                    }
                }

                break;
            default:
                break;
        }
    }

    const onPrivateMessageReceived = (payload) => {
        let payloadData = JSON.parse(payload.body);
        console.log("Received a private message!",payloadData);
        let newCards;

        switch(payloadData.action){
            case "JOIN":
                console.log("setting user ID in join....")
                setUser( (oldUser) => ({...oldUser, ...{connected: true, id:payloadData.id}}));

                break;
            case "DRAW":
                if(payloadData.message === "played"){
                    requestPublicInformation("top card");
                    break;
                }
                newCards = JSON.parse(payloadData.cards);
                newCards.forEach(card => {
                    card.suit = card.suit.toLowerCase();
                    card.rank = card.rank.toLowerCase();
                    card.id = uuidv4();
                    card.selected = false;
                    card.front = true;
                })

               if(payloadData.message.includes("You can't play up to")){
                   let newAnnouncements = payloadData.message.split("\n");
                   newAnnouncements = newAnnouncements.map( (announcement) => ({id: uuidv4(), message:announcement}));
                   setAnnouncements(newAnnouncements);
                   requestPublicInformation("top card");
               }

               else{
                    console.log(payloadData.message + " does not include 'You can't play up to");
                    console.log("Undefined? :" + payloadData.message.includes("You can't play up to"))

               }

//                else{
                    setHand( (oldHand) => ([...oldHand, ...newCards]));
                    setPlayOrder([]);
//                }

                break;
            case "PLAY":
                console.log("setting user ID in play....")
                setUser( (oldUser) => ({...oldUser, ...{connected: true, id:payloadData.id}}));

                //If an empty string was sent
                if(payloadData.message.toLowerCase() === "no"){
                    break;
                }

                //If a card was sent
                else{
                    if(payloadData.message.includes("You have card(s) you can play to avoid the two-card penalty")){
                        console.log("I include 'you have cards to avoid the two card penalty'");
                        let newAnnouncements = payloadData.message.split("\n");
                        let twoCardsCount = Number(payloadData.numStackedTwoCards);
                        setGame((oldGame)=>({...oldGame, ...{numStackedTwoCards:twoCardsCount}}));
                        newAnnouncements = newAnnouncements.map( (announcement) => ({id: uuidv4(), message:announcement}));
                        setAnnouncements(newAnnouncements);

                        break;
                    }

                    else{
                       console.log(payloadData.message + "does not include" + " You have card(s) you can play to avoid the two-card penalty");
                       console.log("Undefined? :" + payloadData.message.includes("You can't play up to"))

                    }

                    newCards = JSON.parse(payloadData.cards);
                    newCards.forEach(card => {
                        card.suit = card.suit.toLowerCase();
                        card.rank = card.rank.toLowerCase();
                        card.id = uuidv4();
                        card.selected = false;
                        card.front = true;
                    })
                    setHand(newCards);
                    setPlayOrder([]);

                    //Update the top card if the card is played
                    if(payloadData.message.toLowerCase() === "yes"){
                        requestPublicInformation("top card");

                    }

                    //The game will tell if you have a card that you can play
                    else if(payloadData.message.toLowerCase().includes("you play")){
                        let newAnnouncements = payloadData.message.split("\n");
                        console.log("message contains 'you play'");
                        newAnnouncements = newAnnouncements.map( (announcement) => ({id: uuidv4(), message:announcement}));
                        setAnnouncements(newAnnouncements)
                        requestPublicInformation("top card");

                    }

                    //If you draw up to 3 cards and still can't play
                    else{
                        let newAnnouncements = payloadData.message.split("\n");
                        newAnnouncements = newAnnouncements.map( (announcement) => ({id: uuidv4(), message:announcement}));
                        setAnnouncements(newAnnouncements);
                        setGame((oldGame)=>({...oldGame, ...{turn:payloadData.turn}}));
                        requestPublicInformation("turn");
                    }

                }


                break;
            default:
                break;
        }

    }

    // const sendPublicMessage = ()=>{
    //     if(stompClient){
    //         let messageToSend = {
    //             name: user.name,
    //             message: user.message,
    //             action:""
    //         };
    //         stompClient.send("/app/message", {}, JSON.stringify(messageToSend));

    //     }
    // }

    const drawCard = ()=>{
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: user.message,
                action: 'DRAW',
            };
            stompClient.send("/app/private-message", {}, JSON.stringify(messageToSend));

        }
    }

    const playCard = () =>{
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: encodeCard(getSelectedCard()),
                action: 'PLAY',
            };
            stompClient.send("/app/private-message", {}, JSON.stringify(messageToSend));
        }
    }






  return (
    <div className="container animate__animated">
        {user.connected?
        <>

        <div className='top'>
            <div className='game-stats'>
                <label>Name: </label>
                <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(user.name)}}/>
                <br></br>
                <label>You are Player: </label>
                <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(user.id)}}/>
                <ul className='player-scores'>
                    <li className='player-1'>
                        <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 1's score: ")}}/>
                        <span id="player-1-score" dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.scores[0].toString())}}/>
                    </li>
                    <li className='player-2'>
                        <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 2's score: ")}}/>
                       <span id="player-2-score"dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.scores[1].toString())}}/>
                    </li>
                    <li className='player-3'>
                        <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 3's score: ")}}/>
                        <span id="player-3-score"dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.scores[2].toString())}}/>
                    </li>
                    <li className='player-4'>
                        <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 4's score: ")}}/>
                        <span id="player-4-score" dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.scores[3].toString())}}/>
                    </li>
                </ul>

                <div className='turn'>
                    <label>Turn:</label>
                    {game.turn > 0?<span id="current-turn" dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player " + game.turn)}}/>
                    :<span id="current-turn">Waiting for all players to join...</span>}
                </div>

                <div className='game-direction'>
                    <label>Game Direction:</label>
                    <span id="current-game-direction" dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.direction)}}/>
                </div>
        </div>

        <div className='game-table'>
            <div className = "game-cards">
                <div className='top-card'>
                    <h2>Top Card</h2>
                    {game.topCard? <Card card={game.topCard} selected={game.topCard.selected} toggleSelectedCard={toggleSelectedCard}></Card>
                    :<div style={{width:"10em", height: "15em", outlineColor:"black", outlineStyle:"solid"}}></div>}
                </div>

                <div className='stockpile-card-container'>
                    <h2>Stockpile</h2>
                    <Card front={false} toggleSelectedCard={toggleSelectedCard} selected={false}/>

                </div>
            </div>
        </div>


        <div className='info-center'>
            <h2>Announcements</h2>
            <div className='announcements'>
                <ul>
                {announcements.length > 0 && announcements.map( announcement =>(
                    <li key={announcement.id} className="annoucement">
                        <p dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(announcement.message)}}/>
                    </li>
                ))}


                </ul>

            </div>


            <div className='action-buttons'>
                <button id='play-card-button' disabled={game.turn!==user.id} onClick={playCard}>Play Card(s)</button>
                <button id='draw-card-button' disabled={game.turn!==user.id} onClick={drawCard}>Draw card</button>

                {game.topCard && game.topCard.rank === "8" && <div className="select-suit">
                    <input id="suit-textbox" ref={suitInput} placeholder = "Enter suit you want to change to"></input>
                    <button id='send-suit-button' onClick={sendSuit}>Send</button>
                </div>}

            </div>

        </div>

        </div>


         <div className='bottom'>
            <div className='hand'>
                <h2>Hand</h2>
                <Cards cardsList={[...hand]} toggleSelectedCard={toggleSelectedCard}/>
            </div>

            <ul className="selected-cards">
                <label>Card Play Order: </label>
{/* //                {getSelectedCard()?<span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(getSelectedCard().rank.toUpperCase() + " " + getSelectedCard().suit.toUpperCase())}}></span> */}
             {/* :<span>Click on a card to see</span>} */}
                  {playOrder.length < 1? <span>Click on a card to see</span>
                  :playOrder.map( (card, index) => {
                    if(index != playOrder.length-1){
                        return <span key={card.id} dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(card.rank.toUpperCase() + " " + card.suit.toUpperCase() + "&#8594")}}/>
                    }

                    else{
                        return <span key={card.id} dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(card.rank.toUpperCase() + " " + card.suit.toUpperCase())}}/>
                    }
                    }

                  )}
            </ul>
         </div>


        </>
        :
        <div className='join-game'>
            <input id="name-textbox" placeholder='Enter your name here' value={user.name} onChange={handleUsername}/>
            <button id="join-button" onClick={registerUser}>Join Game</button>
        </div>}
    </div>

  )
}

export default PlayRoom
// current top card
// whose turn it is
//player's hand
//game direction
//PROMPT the player for the next suit when they play an 8
//