import "./index.css"

function Card( {card, selected, toggleSelectedCard} ) {

  const getCardImage = () => {
    let url = "/images/"
    if(!card){
        url += "back_card/blue.png";
        return url;
    }

    if (card.front){
        url += "front_cards/" + card.suit + "_" + card.rank + ".png";
    }

    else{
      url += "back_card/blue.png";
  }
    return url;
  }

  const getClassName = () => {
    let newClassName;
    if(!card){
      newClassName = "stockpile card";
    }

    else{
      newClassName = card.rank + "-" + card.suit + " card";

      if(selected){
        newClassName+= " active";
      }
    }

    return newClassName;

  }


  function handleCardClicked(){;
    if(card){
        toggleSelectedCard(card.id);
    }   
  
  }

  return (
    <>
      <div className={getClassName()} onClick={handleCardClicked}>
        <img src={getCardImage()} alt={card?card.rank + " " + card.suit:"stockpile"}></img>
      </div>

    </>

  )
}

export default Card