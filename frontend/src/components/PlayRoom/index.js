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
        message:"",
        score:"0",
        id:null
    })

    const [hand, setHand] = useState([])
    const [game, setGame] = useState({
        topCard: null,
        turn: 0,
        direction: "Left",
        scores:["0","0","0","0"]
    })

    const [cardsToPlay, setCardsToPlay] = useState([]);
    const [announcements, setAnnouncements] = useState([]);
    const usernameTextBox = useRef();

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
        console.log("storing this announcement array", announcements)
        setAnnouncements(storedAnnouncements);
      }

    }, [])

    useEffect(() => {
        sessionStorage.setItem(GAME_SESSION_STORAGE_KEY, JSON.stringify(game));
      }, [game])

      useEffect(() => {
        console.log("Saving this user:", user);
        sessionStorage.setItem(PLAYER_SESSION_STORAGE_KEY, JSON.stringify(user));
      }, [user])

      useEffect(() => {
        console.log("Saving this announcement array:", announcements)
        sessionStorage.setItem(ANNOUNCEMENTS_STORAGE_KEY, JSON.stringify(announcements));
      }, [announcements])


      useEffect( () => {
        console.log("Current Announcements", announcements);
      }, [announcements])

      useEffect( () => {
        console.log("Current User", user);
      }, [user])

      
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

  const saveUsername = () =>{
    let username = usernameTextBox.current.value;
    setUser({...user, ...{name: username}})
  }

  // Networking functions
    const registerUser = () => {
        let Sock = new SockJS("http://localhost:8080/ws")
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    }

    const onConnected = () =>{
        saveUsername();
        stompClient.subscribe("/playroom/public", onPublicMessageReceived);
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
    }

    const onPublicMessageReceived = (payload) => {
        let payloadData = JSON.parse(payload.body);
        switch(payloadData.action){
            case "JOIN":
              let newUserState 
              if(!user.id){
                newUserState = {...user, ...{connected:true, id:payloadData.id}}
                stompClient.subscribe("/player/" + payloadData.id + "/private", onPrivateMessageReceived);
              }

              announcements.push({id:uuidv4(), message:payloadData.message});
              setAnnouncements([...announcements]);
              setUser(newUserState);
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
                action:""
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
                action: 'MESSAGE',
                id:user.id
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



  return (
    <div className="container">
        {user.connected? 
        <>

        <div className='top'>
        <div className='game-stats'>
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
            <input type="text" id="join-textbox" placeholder='Enter your name here' ref={usernameTextBox}/>
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