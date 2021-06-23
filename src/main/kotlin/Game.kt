import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.*

//used only for reproducer
val startCards= arrayListOf(
    PlayCard("Mammoth"),
    PlayCard("Chloe Frazer",),
    PlayCard("Judy Alvarez",),
    PlayCard("Aloy",),
    PlayCard("John Wick",),
    PlayCard("Ellie",),
)

interface GameCallback {
    fun onNewCard()
}

class Game() {
    private val handRowCallback = mutableListOf<GameCallback>()
    private val playerRowCallback = mutableListOf<GameCallback>()

    //cards of each row (actual state of the game)
    val handCards= mutableStateListOf<PlayCard>()
    val playerRowCards = mutableStateListOf<PlayCard>()

    init {
        startCards.forEach { pc: PlayCard ->
            handCards.add(PlayCard(pc.name))
        }
    }

    //When is moved to the upper row, it is added to it and removed from the old one
    //the callbacks notify the observers
    fun cardToPlayerRow(card: PlayCard) {
        playerRowCards.add(card)
        handCards.remove(card)

        println("before")

        handRowCallback.forEach{it.onNewCard()}
        playerRowCallback.forEach { it.onNewCard() }
        println("after notifs")
    }

    fun registerToPlayerRow(callback: GameCallback) {
        playerRowCallback.add(callback)
    }

    fun unregisterToPlayerRow(callback: GameCallback) {
        playerRowCallback.remove(callback)
    }

    fun registerToHandRow(callback: GameCallback) {
        handRowCallback.add(callback)
    }

    fun unregisterToHandRow(callback: GameCallback) {
        handRowCallback.remove(callback)
    }
}

@Composable
fun getHandCards(game: Game): State<MutableList<PlayCard>> {
    var cards = remember { mutableStateOf(game.handCards) }
    DisposableEffect(game) {
        val callback =
            object : GameCallback {
                override fun onNewCard() {
                    cards.value=game.handCards
                }
            }
        game.registerToHandRow(callback)
        onDispose { game.unregisterToHandRow(callback) }
    }
    return cards
}

@Composable
fun getPlayerRowCards(game: Game): State<MutableList<PlayCard>>{
    var cards = remember { mutableStateOf(game.playerRowCards) }
    DisposableEffect(game) {
        val callback =
            object : GameCallback {
                override fun onNewCard() {
                    cards.value=game.playerRowCards
                    println("player row callback")
                }
            }
        game.registerToPlayerRow(callback)
        onDispose { game.unregisterToPlayerRow(callback) }
    }
    return cards
}

@Composable
fun Board(game: Game) {
    Column(
        modifier = Modifier.fillMaxSize(1f),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(180.dp)
                .background(Color.Gray)
        ) { //row empty in the reproducer
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(180.dp)
                .background(Color.Gray)
        ) {
            //retrieve the actual state of the row using the callbacks
            getPlayerRowCards(game).value.forEach { pc ->
                DisplayCard(card = pc,
                    isMovableUp = false,
                    isMovableDown = false,
                    onDragEndUp = {},
                    onDragEndDown = {})
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(180.dp)
                .background(Color.Gray)
        ) {
            getHandCards(game).value.forEach { pc: PlayCard ->
                DisplayCard(modifier = Modifier,
                    card = pc,
                    isMovableUp = getPlayerRowCards(game).value.size< 4,
                    isMovableDown = false,
                    onDragEndUp = {game.cardToPlayerRow(pc)},
                    onDragEndDown = {})
            }
        }
    }
}

//This part of code didn't change when I tried working on the new callbacks.
//I don't think the problem is nested here
@Composable
fun DisplayCard(
    modifier: Modifier = Modifier,
    card: PlayCard,
    isMovableUp: Boolean,
    isMovableDown: Boolean,
    onDragEndUp: () -> Unit,
    onDragEndDown: () -> Unit
) = key(card, isMovableUp, isMovableDown) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val startY = offsetY
    val startX = offsetX
    Box(
        modifier = modifier
            .offset(offsetX.dp, offsetY.dp)
            .width(100.dp).height(180.dp)
            .border(width = 2.dp, color = Color.Red)
            .pointerInput(key1 = null) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                            if(isMovableUp || isMovableDown)
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        if (isMovableUp && startY > offsetY) {
                            onDragEndUp()
                        } else if (isMovableDown && startY < offsetY) {
                            onDragEndDown()
                        } else {
                            offsetY=startY
                            offsetX=startX
                        }
                    })
            },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxSize()
                    .background(Color.White),
            ) {}
            Box(
                modifier = Modifier.weight(1f)
                    .fillMaxSize()
                    .background(color = Color.Yellow)
            )
            {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
                ) {
                    Text(text = card.name,)
                }
            }
        }
    }
}

class PlayCard(val name: String) {}

fun main(args: Array<String>): Unit {
    System.setProperty("skiko.renderApi", "OPENGL")
    Window(title = "HEIG game", size = IntSize(700, 800)) {
        Board(Game())
    }
}