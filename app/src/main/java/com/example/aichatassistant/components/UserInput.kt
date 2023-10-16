package com.example.aichatassistant.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aichatassistant.api.postAiMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
@Composable
fun ChatBox(messageModel: MessageModel){
    var isAiThinking by remember {mutableStateOf(false)}
    var showSend by remember {mutableStateOf(true)}
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var textMessage by rememberSaveable { mutableStateOf("") }
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onTertiary)
    ){
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 15.dp, top = 3.dp, bottom = 3.dp, end = 5.dp)
        ){
            BasicTextField(
                value = textMessage,
                onValueChange = { textMessage = it },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        if (textMessage.isEmpty()) {
                            Text(
                                text = "Enter message...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.LightGray
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            IconButton(
                onClick = {
                    if(textMessage == ""){
                        Toast.makeText(context, "Add message first!", Toast.LENGTH_SHORT).show()
                    }else{
                        val messageUsage = textMessage.trim()
                        messageModel.addMessage(Message(messageUsage,1,true))
                        textMessage = ""
                        showSend = false
                        isAiThinking = true
                        coroutineScope.launch(handler) {
                            //testPublicApiPost()
                            messageModel.toggleSentStat(messageModel.getMessageList.size-1)
                            val result = postAiMessage(messageUsage)
                            println(result);
                            messageModel.addMessage(Message(result,0,true))
                            isAiThinking = false
                            showSend = true
                        }
                    }
                },
                interactionSource = remember { MutableInteractionSource() },
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                enabled = showSend
            ) {
                AnimatedVisibility(visible = showSend) {
                    Icon(Icons.Filled.Send, contentDescription = "Send",
                        modifier = Modifier
                            .rotate(-45f)
                            .size(23.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                AnimatedVisibility(visible = isAiThinking) {
                    DotsTyping()
                }
            }
        }
    }
}

val dotSize = 7.dp // made it bigger for demo
val delayUnit = 300 // you can change delay to change animation speed
@Composable
fun DotsTyping() {
    val maxOffset = 10f
    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(y = -offset.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = CircleShape
            )
    )
    val infiniteTransition = rememberInfiniteTransition()
    @Composable
    fun animateOffsetWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                maxOffset at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )
    val offset1 by animateOffsetWithDelay(0)
    val offset2 by animateOffsetWithDelay(delayUnit)
    val offset3 by animateOffsetWithDelay(delayUnit * 2)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        val spaceSize = 2.dp
        Dot(offset1)
        Spacer(Modifier.width(spaceSize))
        Dot(offset2)
        Spacer(Modifier.width(spaceSize))
        Dot(offset3)
    }
}

@Preview
@Composable
fun ShowUserInput(){
    ChatBox(messageModel = MessageModel())
}