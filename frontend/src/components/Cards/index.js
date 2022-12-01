import Card from '../Card';
import "./index.css"


function Cards( {cardsList, toggleSelectedCard} ) {
  return <ul>
    <li className='cardsList'>
      {cardsList.map( card => (
      <Card key={card.id} card={{...card}} toggleSelectedCard={toggleSelectedCard} selected={card.selected}></Card>))}
    </li>
  </ul>
}

export default Cards