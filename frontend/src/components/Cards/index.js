import Card from '../Card';
import "./index.css"


function Cards( {cardsList, toggleSelectedCard} ) {
  return (
  <ul className='cardsList'>
      {cardsList.map( card => (<li  key={card.id}>
        <Card card={{...card}} toggleSelectedCard={toggleSelectedCard} selected={card.selected}></Card>
      </li>
      ))}
  </ul>)
}

export default Cards