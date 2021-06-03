import androidx.compose.desktop.Window
import androidx.compose.ui.unit.IntSize

fun main(args: Array<String>): Unit {
    System.setProperty("skiko.renderApi", "OPENGL")

    Window(title = "HEIG game", size = IntSize(700, 1010)) {

        val cards= arrayListOf(
            PlayCard("Mammoth", "player1", 0),
            PlayCard("Chloe Frazer", "player1", 1),
            PlayCard("Judy Alvarez", "player1", 2),
            PlayCard("Aloy", "player1", 3),
            PlayCard("John Wick", "player1", 4),
            PlayCard("Ellie", "player1", 5),
            PlayCard("Glinthawk", "player1", 6),
            PlayCard("Asari warrior", "player1", 7),
            PlayCard("Ahsoka Tano", "player1", 8),
            PlayCard("Geralt of Rivia", "player1", 9),
        )

        val player=Player(pseudo = "player1",
            playDeck = PlayDeck("deck1",cards)
        )

        val game = Game(
            player = player,)
        Board(game)
    }
}




