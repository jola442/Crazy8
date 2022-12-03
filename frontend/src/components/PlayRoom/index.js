import { useEffect, useState, useRef } from 'react'
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

function PlayRoom() {
    const [user, setUser] = useState({
        name:"",
        connected: false,
        score:"0",
        id:"-1",
    })

    const [hand, setHand] = useState([])
    const [game, setGame] = useState({
        topCard: null,
        turn: "1",
        direction: "Left",
        scores:["0","0","0","0"]
    })

    const [cardsToPlay, setCardsToPlay] = useState([]);
    const [announcements, setAnnouncements] = useState([]);

    useEffect(() => {
    console.log("a re-render happened");
      const storedGame = JSON.parse(sessionStorage.getItem(GAME_SESSION_STORAGE_KEY));
      const storedPlayer = JSON.parse(sessionStorage.getItem(PLAYER_SESSION_STORAGE_KEY));
      const storedAnnouncements = JSON.parse(sessionStorage.getItem(ANNOUNCEMENTS_STORAGE_KEY));
    
      if(storedGame){
        setGame(storedGame)
      }

      if(storedPlayer){
        setUser(storedPlayer);
      }

      if(storedAnnouncements){
        setAnnouncements(storedAnnouncements);
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


      // useEffect( () => {
      //   console.log("Current Announcements", announcements);
      // }, [announcements])

    //   useEffect( () => {
    //     console.log("Current User", user);
    //   }, [user])

      
      useEffect( () => {
        console.log("Current hand", hand);
      }, [hand])

      
    function handleCardToPlay(card){
      let cardIndex = cardsToPlay.indexOf(card);
      let newCardsToPlay;
      if(cardIndex === -1){
          newCardsToPlay = [...cardsToPlay]
          newCardsToPlay.push(card)
          setCardsToPlay(newCardsToPlay);
      }

      else{
          console.log(cardIndex);
          newCardsToPlay = [...cardsToPlay]
          newCardsToPlay.splice(cardIndex, 1);
          setCardsToPlay(newCardsToPlay);
      }
  }

  const handleUsername=(event)=>{
    const {value}=event.target;
    setUser({...user,"name": value});
}

  // Networking functions
    const registerUser = () => {
        let Sock = new SockJS("http://localhost:8080/ws")
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    }

    const onConnected = () =>{
        // saveUsername();
        setUser( (oldUser) => ({...oldUser, ...{connected:true}}));
        stompClient.subscribe("/playroom/public", onPublicMessageReceived);
        console.log("current username:", user.name);
        stompClient.subscribe('/player/'+user.name+'/private', onPrivateMessageReceived);
        userJoins();
    }

    const onError = (error) => {
        console.log(error);
    }
    const userJoins = () =>{
        let message = {
            name: user.name,
            message: user.message,
            action: 'JOIN'
        };
        stompClient.send("/app/message", {}, JSON.stringify(message));
        requestID();
    }

    function requestID(){
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: "",
                action: 'JOIN',
            };
            stompClient.send("/app/private-message", {}, JSON.stringify(messageToSend));
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

    function requestTopCard(){
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: "top card",
                action:"DRAW"
            };
            stompClient.send("/app/message", {}, JSON.stringify(messageToSend));
          
        }
    }

    const onPublicMessageReceived = (payload) => {
        let payloadData = JSON.parse(payload.body);
        switch(payloadData.action){
            case "JOIN":
                setAnnouncements((oldAnnouncements) => ([...oldAnnouncements, {id:uuidv4(), message:payloadData.message}]))
                setGame((oldGame)=>({...oldGame, ...{turn:payloadData.turn}}));
                if(payloadData.numPlayers === "4"){
                    requestStartingCards();
                    requestTopCard();
                }
                break;
            case "DRAW":
                let payloadCards = JSON.parse(payloadData.cards);
                const newTopCard = payloadCards[0];
                newTopCard.id = uuidv4();
                newTopCard.selected = false;
                newTopCard.front = true;    
                setGame((oldGame)=>({...oldGame, ...{turn:payloadData.turn, topCard:newTopCard}}));
                break;
            default:
                break;
        }
    }

    const onPrivateMessageReceived = (payload) => {
        let payloadData = JSON.parse(payload.body);
        console.log("Received a private message!",payloadData);

        switch(payloadData.action){
            case "JOIN":
                setUser( (oldUser) => ({...oldUser, ...{id:payloadData.id}}));
                break;
            case "DRAW":
                let newCards = JSON.parse(payloadData.cards);
                newCards.forEach(card => {
                    card.id = uuidv4();
                    card.selected = false;
                    card.front = true;    
                })
                setHand( (oldHand) => ([...oldHand, ...newCards]));
                break;
            default:
                break;
        }
    
    }

    const sendPublicMessage = ()=>{
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: user.message,
                action:""
            };
            stompClient.send("/app/message", {}, JSON.stringify(messageToSend));
          
        }
    }

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

    function toggleSelectedCard(id){
        const newHand = [...hand];
        const selectedCard = newHand.find( card => card.id === id);
        if(selectedCard){
            selectedCard.selected = !selectedCard.selected;
            setHand(newHand);
            handleCardToPlay(selectedCard);
        }

    }



  return (
    <div className="container">
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
                        <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.scores[0].toString())}}/>
                    </li>
                    <li className='player-2'>
                        <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 2's score: ")}}/>
                        <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.scores[1].toString())}}/>
                    </li>
                    <li className='player-3'>
                        <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 3's score: ")}}/>
                        <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.scores[2].toString())}}/>
                    </li>
                    <li className='player-4'>
                        <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 4's score: ")}}/>
                        <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.scores[3].toString())}}/>
                    </li>
                </ul>

                <div className='turn'>
                    <label>Turn:</label>
                    <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player " + game.turn)}}/>
                </div>

                <div className='game-direction'>
                    <label>Game Direction:</label>
                    <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.direction)}}/>
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
                {announcements.length > 0 && announcements.map( announcement =>(
                  <p className="annoucement" key = {announcement.id} dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(announcement.message)}}/>
                ))}
            </div>


            <div className='action-buttons'>
                <button id='play-card-button' onClick={sendPublicMessage}>Play Card(s)</button>
                <button id='draw-card-button' onClick={drawCard}>Draw card</button>
            </div>

        </div>

        </div>

         
         <div className='bottom'>
            <div className='hand'>
                <h2>Hand</h2>
                <Cards cardsList={[...hand]} toggleSelectedCard={toggleSelectedCard}/>
            </div>

            <ul className="play-order">
                <label>Current card play order:</label>
                {cardsToPlay.length > 0 && cardsToPlay.map( (card) =>(<span key={uuidv4()} dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(card.rank + " " + card.suit + "&#8594")}}></span>))}
            </ul>
         </div>


        </>
        :
        <div className='join-game'>
            <input id="join-textbox" placeholder='Enter your name here' value={user.name} onChange={handleUsername}/>
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