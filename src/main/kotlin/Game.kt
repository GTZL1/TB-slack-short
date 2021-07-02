import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class DeckType(val id: Int, var name: String) {}

class DeckGUI(
    decksList: MutableList<DeckType>
) {
    val decks by mutableStateOf(decksList)
    val deck = mutableStateOf(decks.first())
    var deckName =mutableStateOf(deck.value.name)

    internal fun newDeck() {
        decks.add(DeckType((-1), UUID.randomUUID().toString().take(15)))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeckScreen(deckGUI: DeckGUI) {
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(100.dp)
                .background(color = MaterialTheme.colors.primary)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeckChoiceMenu(deckGUI.decks, deckGUI.deck)
            TextField(
                value = deckGUI.deckName.value,
                onValueChange = { value ->
                    deckGUI.deckName.value = value
                    deckGUI.deck.value.name=value},
                label = {
                    Text(
                        text = "Deck name",
                        color = Color.White
                    )
                },
            )
            Button(modifier = Modifier.height(50.dp).padding(horizontal = 10.dp),
                onClick = {
                    deckGUI.newDeck()
                }){
                Text(text = "New deck",
                    color = Color.White)
            }

        }
    }
}

@Composable
private fun DeckChoiceMenu(
    decks: List<DeckType>,
    deck: MutableState<DeckType>
) = key(decks, deck) {
    var expanded by remember { mutableStateOf(false) }
    Text(text = deck.value.name,
        modifier = Modifier.clickable(onClick = {
            expanded=!expanded })
            .width(250.dp)
            .padding(end = 50.dp),
        color= Color.White,)
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded=false},
        content= {
            decks.forEach { deckType: DeckType ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    deck.value=deckType
                }){
                    Text(text = deckType.name)
                }
            }
        })
}

fun main(args: Array<String>): Unit {
    System.setProperty("skiko.renderApi", "OPENGL")
    Window(title = "HEIG game", size = IntSize(700, 800)) {
        DeckScreen(DeckGUI(mutableListOf(DeckType(1, "default"))))
    }
}