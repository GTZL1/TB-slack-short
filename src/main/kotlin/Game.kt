//Code of the Composable file - problem is probably nested here

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

interface GameCallback {
    fun onNewData(pc: PlayCard)
}

interface GameInterface {
    fun cardToPlayerRow(card: PlayCard)
    fun cardToCenterRow(card: PlayCard)
    fun registerToPlayerRow(callback: GameCallback)
    fun unregisterToPlayerRow(callback: GameCallback)
    fun registerToCenterRow(callback: GameCallback)
    fun unregisterToCenterRow(callback: GameCallback)
}

class Game(
    val player: Player,
    //usually there is also an opponent, but it is not included here
) : GameInterface {
    private val playerRowCallback = mutableListOf<GameCallback>()
    private val centerRowCallback = mutableListOf<GameCallback>()

    override fun cardToPlayerRow(card: PlayCard) {
        playerRowCallback.forEach { it.onNewData(pc = card) }
    }

    override fun cardToCenterRow(card: PlayCard) {
        centerRowCallback.forEach { it.onNewData(pc = card) }
    }

    override fun registerToPlayerRow(callback: GameCallback) {
        playerRowCallback.add(callback)
    }

    override fun unregisterToPlayerRow(callback: GameCallback) {
        playerRowCallback.remove(callback)
    }

    override fun registerToCenterRow(callback: GameCallback) {
        centerRowCallback.add(callback)
    }

    override fun unregisterToCenterRow(callback: GameCallback) {
        centerRowCallback.add(callback)
    }
}

@Composable
fun Board(game: Game) {
    val handCards = remember { mutableStateListOf<PlayCard>() }
    DisposableEffect(Unit) {
        game.player.hand.getAllCards().forEach { pc: PlayCard ->
            handCards.add(PlayCard(pc.name, pc.owner, pc.id))
        }
        onDispose { }
    }

    val playerRowCards = remember { mutableStateListOf<PlayCard>() }
    DisposableEffect(game) {
        val callback =
            object : GameCallback {
                override fun onNewData(pc: PlayCard) {
                    playerRowCards.add(pc)
                    handCards.remove(pc)
                }
            }
        game.registerToPlayerRow(callback)
        onDispose { game.unregisterToPlayerRow(callback) }
    }

    val centerRowCards = remember { mutableStateListOf<PlayCard>() }
    DisposableEffect(game) {
        val callback =
            object : GameCallback {
                override fun onNewData(pc: PlayCard) {
                    centerRowCards.add(pc)
                    playerRowCards.remove(pc)

                }
            }
        game.registerToCenterRow(callback)
        onDispose { game.unregisterToCenterRow(callback) }
    }

    Column(
        modifier = Modifier.fillMaxSize(1f),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(180.dp)
                .background(Color.Gray)
        ) {
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(180.dp)
                .background(Color.Gray)
        ) {
            centerRowCards.forEach { pc ->
                DisplayCard(card = pc,
                    onDragEndUp = {},
                    onDragEndDown = {
                        //future callback to put cards in playerRowCards
                    })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(180.dp)
                .background(Color.Gray)
        ) {
            playerRowCards.map { pc ->
                DisplayCard(card = pc,
                    onDragEndUp = {
                        game.cardToCenterRow(pc)
                    },
                    onDragEndDown = {})
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(180.dp)
                .background(Color.Gray)
        ) {
            handCards.map { pc: PlayCard ->
                DisplayCard(modifier = Modifier,
                    card = pc,
                    onDragEndUp = {
                        game.cardToPlayerRow(pc)
                    },
                    onDragEndDown = {})
            }
        }
    }
}

@Composable
fun DisplayCard(
    modifier: Modifier = Modifier,
    card: PlayCard,
    onDragEndUp: () -> Unit,
    onDragEndDown: () -> Unit
) {
    val currentOnDragEndUp by rememberUpdatedState(onDragEndUp)
    val currentOnDragEndDown by rememberUpdatedState(onDragEndDown)
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var start = offsetY
    Box(
        modifier = modifier
            .offset(offsetX.dp, offsetY.dp)
            .width(100.dp).height(180.dp)
            .clip(shape = CutCornerShape(5.dp))
            .border(width = 2.dp, color = Color.Red, shape = CutCornerShape(5.dp))
            .pointerInput(currentOnDragEndDown, currentOnDragEndUp) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        if (start > offsetY) {
                            currentOnDragEndUp()
                        } else if (start < offsetY) {
                            currentOnDragEndDown()
                        }
                        start = offsetY
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
            ) {} //usually there is the card image in this box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(color = Color.Yellow)
            )
            {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = card.name,
                        style = TextStyle(
                            fontFamily = FontFamily.Default, fontSize = 14.sp, fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}