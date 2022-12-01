import React, { useEffect, useState } from 'react'
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

function PlayRoom() {
    const [user, setUser] = useState({
        name:"",
        connected: false,
        message:"",
        score:"0",
        id:"0"
    })

    const [hand, setHand] = useState([{id: uuidv4(), suit:"clubs", rank:"ace", front:true, selected:false}, {id: uuidv4(), suit:"diamonds", rank:5, front:true, selected:false}, {id: uuidv4(), suit:"diamonds", rank:4, front:true, selected:false}, {id: uuidv4(), suit:"hearts", rank:2, front:true, selected:false},{id: uuidv4(), suit:"spades", rank:"queen", front:true, selected:false}, {id: uuidv4(), suit:"hearts", rank:"king", front:true, selected:false}])
    const [game, setGame] = useState({
        topCard: {id:uuidv4(), suit:"clubs", rank:5, front:true, selected:false},
        turn: 1,
        direction: "Left",
    })

    const [cardsToPlay, setCardsToPlay] = useState([]);

    useEffect(() => {
    console.log("a re-render happened");
      const storedGame = JSON.parse(sessionStorage.getItem(GAME_SESSION_STORAGE_KEY));
      const storedPlayer = JSON.parse(sessionStorage.getItem(PLAYER_SESSION_STORAGE_KEY));
    
      if(storedGame){
        setGame(storedGame)
      }

      if(storedPlayer){
        setUser(storedPlayer);
      }

    }, [])

    useEffect(() => {
        sessionStorage.setItem(GAME_SESSION_STORAGE_KEY, JSON.stringify(game));
      }, [game])

      useEffect(() => {
        sessionStorage.setItem(PLAYER_SESSION_STORAGE_KEY, JSON.stringify(user));
      }, [user])

    
    const handleName = (event)=>{
        const newUserName = event.target.value;
        setUser({...user, ...{name: newUserName}})
    }

    const registerUser = () => {
        let Sock = new SockJS("http://localhost:8080/ws")
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    }

    const onConnected = () =>{
        setUser({...user, ...{connected:true}});
        stompClient.subscribe("/playroom/public", onPublicMessageReceived);
        stompClient.subscribe("/player/" + user.name + "/private", onPrivateMessageReceived);
        userJoins();
    }

    const onError = (error) => {
        console.log(error);
    }
    const userJoins = () =>{
        let message = {
            name: user.name,
            message: user.message,
            status: 'JOIN'
        };
        stompClient.send("/app/message", {}, JSON.stringify(message));
    }

    const onPublicMessageReceived = (payload) => {
        let payloadData = JSON.parse(payload.body);
        switch(payloadData.status){
            case "JOIN":
              break;
            case "MESSAGE":
                setGame(...game, ...{topCard: payloadData.message});
                break; 
            default:
                break;
        }
    }

    const onPrivateMessageReceived = (payload) => {
        let payloadData = JSON.parse(payload.body);
        setUser({...user, ...{score: payloadData.score}})
        console.log(payloadData.message);
    }

    const sendPublicMessage = ()=>{
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: user.message,
                status: 'MESSAGE'
            };
            stompClient.send("/app/message", {}, JSON.stringify(messageToSend));
            setUser({...user, ...{message:""}});
        }
    }

    const sendPrivateMessage = ()=>{
        if(stompClient){
            let messageToSend = {
                name: user.name,
                message: user.message,
                status: 'MESSAGE'
            };
            stompClient.send("/app/private-message", {}, JSON.stringify(messageToSend));
            setUser({...user, ...{message:""}});
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


  return (
    <div className="container">
        {user.connected? 
        <>

        <div className='top'>
        <div className='game-stats'>
        <ul className='player-scores'>
            <li className='player-1'>
                <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 1's score: ")}}/>
                <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.playerOneScore)}}/>
            </li>
            <li className='player-2'>
                <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 2's score: " + game.playerTwoScore)}}/>
                <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.playerTwoScore)}}/>
            </li>
            <li className='player-3'>
                <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 3's score: " + game.playerThreeScore)}}/>
                <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.playerThreeScore)}}/>
            </li>
            <li className='player-4'>
                <label dangerouslySetInnerHTML={{__html: DOMPurify.sanitize("Player 4's score: " + game.playerFourScore)}}/>
                <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(game.playerFourScore)}}/>
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
                    <Card card={game.topCard} selected={game.topCard.selected} toggleSelectedCard={toggleSelectedCard}></Card>
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
                
            </div>


            <div className='action-buttons'>
                <button id='play-card-button' onClick={sendPublicMessage}>Play Card(s)</button>
                <button id='draw-card-button' onClick={sendPrivateMessage}>Draw card</button>
            </div>

        </div>

        </div>

         
         <div className='bottom'>
            <div className='hand'>
                <h2>Hand</h2>
                <Cards cardsList={[...hand]} toggleSelectedCard={toggleSelectedCard}/>
            </div>
         </div>


        </>
        :
        <div className='join-game'>
            <input id="join-textbox" placeholder='Enter your name here' value={user.name} onChange={handleName}/>
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