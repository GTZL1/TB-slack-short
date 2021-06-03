class Player(val pseudo: String, val playDeck: PlayDeck) {

    val hand=Hand(playDeck.drawMultipleCards(6))
}