class Hand(cards: List<PlayCard>) {
    private val cards= arrayOfNulls<PlayCard>(6)

    init {
        for(x in 0 until 6){
            this.cards[x]= cards[x]
        }
    }

    fun getAllCards():List<PlayCard>{
        return cards.filterNotNull()
    }

    fun putCardOnBoard(card: PlayCard): PlayCard{
        println("Removing "+card.name)
        val index=cards.filterNotNull().indexOfFirst {
            playCard: PlayCard -> playCard.name == card.name &&
                playCard.owner == card.owner &&
                playCard.id == card.id
        }
        val cardDrawn=cards.filterNotNull().get(index)
        cards[index]=null
        return cardDrawn
    }
}