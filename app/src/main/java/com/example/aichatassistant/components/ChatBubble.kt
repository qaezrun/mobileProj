package com.example.aichatassistant.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aichatassistant.R
import com.example.aichatassistant.ui.theme.AiChatAssistantTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun Response(from: Int, message: String){
    var messageSent by remember { mutableStateOf(false) }
    var isBoxVisible by remember {mutableStateOf(false)} // hide time of chat
    var cardWidth by remember { mutableStateOf(0) }
    var hideImage by remember { mutableStateOf(false) } // hide image if user chats
    var imgAlignment by remember { mutableStateOf(Alignment.CenterVertically) } // alignment of image on chat bubble
    var rowAlignment by remember { mutableStateOf(Alignment.CenterStart) }// alignment of bubble
    var layoutDirection by remember { mutableStateOf(LayoutDirection.Ltr) } // layout direction of bubble
    val aiColor = MaterialTheme.colorScheme.onTertiary //chat bubble color for ai
    val userColor = MaterialTheme.colorScheme.primary // chat bubble color for user
    var containerColor by remember { mutableStateOf(aiColor) }
    val formattedTime = getCurrentTime()


    if(from == 0){
        layoutDirection = LayoutDirection.Ltr
        containerColor = aiColor
        rowAlignment = Alignment.CenterStart
        hideImage = false
    }else{
        layoutDirection = LayoutDirection.Rtl
        containerColor = userColor
        rowAlignment = Alignment.CenterEnd
        hideImage = true
    }

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)
    ){
        CompositionLocalProvider (
            LocalLayoutDirection provides layoutDirection,
        ){
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .align(rowAlignment)
            ){
                if(message.length > 200){
                    imgAlignment = Alignment.Top
                }
                if(!hideImage){
                    Image(painter = painterResource(R.drawable.elon), contentDescription = "aiProfile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .align(imgAlignment)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }else{
                    if(!messageSent){
                        Icon(
                            Icons.Filled.Send, contentDescription = "Send",
                            modifier = Modifier
                                .rotate(-45f)
                                .size(16.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ){
                            isBoxVisible = !isBoxVisible
                        }
                ){
                    Card (elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 15.dp,
                            bottomStart = 15.dp,
                            bottomEnd = 15.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor,
                        ),
                        modifier = Modifier
                            .widthIn(min = 0.dp, max = 250.dp)
                            .onSizeChanged { size: IntSize ->
                                cardWidth = size.width
                            }

                    ) {
                        Text(
                            text = message,
                            fontSize = 16.sp,
                            softWrap = true,
                            textAlign = TextAlign.Justify,
                            style = TextStyle(textDirection = TextDirection.Content),
                            modifier = Modifier.padding(10.dp)
                        )
                        AnimatedVisibility(visible = isBoxVisible) {
                            Box(
                                modifier = Modifier
                                    .width(with(LocalDensity.current){cardWidth.toDp()}),
                                    contentAlignment = Alignment.CenterEnd
                            ){

                                Text(
                                    modifier = Modifier
                                        .padding(end = 10.dp, bottom = 5.dp),
                                    text = formattedTime,
                                    fontSize = 10.sp,
                                    style = TextStyle(textDirection = TextDirection.Content)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
fun getCurrentTime(): String {
    val currentTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("h:mm a", Locale.US)
    return dateFormat.format(currentTime)
}
@Preview
@Composable
fun ShowProgress(){
   AiChatAssistantTheme {
       Surface {
            Column {
                Response(0,"Daniel, I hope this message finds you well. As we move forward, there have been some changes in our project plans that I wanted to discuss with you. Please make some time for a meeting to go over the details Daniel, I hope this message finds you well. As we move forward, there have been some changes in our project plans that I wanted to discuss with you. Please make some time for a meeting to go over the details Hey Sarah, it's time for your meeting. Don't be late, and be prepared.")
                Response(1,"John, your project deadline is approaching. Ensure everything is on track.")
                Response(0,"Remember, Mia, to attend the conference call at 3 PM sharp.")
            }
       }
   }
}
