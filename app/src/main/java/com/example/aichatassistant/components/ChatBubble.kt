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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun Response(from: Int, message: String){

    var isBoxVisible by remember {mutableStateOf(false)}
    var userImage by remember { mutableIntStateOf(R.drawable.elon)}
    var imgAlignment by remember { mutableStateOf(Alignment.CenterVertically) }
    var rowAlignment by remember { mutableStateOf(Alignment.CenterStart) }
    var layoutDirection by remember { mutableStateOf(LayoutDirection.Ltr) }
    val aiColor = MaterialTheme.colorScheme.surfaceVariant
    val userColor = MaterialTheme.colorScheme.primary
    var containerColor by remember { mutableStateOf(aiColor) }
    val formattedTime = getCurrentTime()

    if(from == 0){
        userImage = (R.drawable.elon)
        layoutDirection = LayoutDirection.Ltr
        containerColor = aiColor
        rowAlignment = Alignment.CenterStart
    }else{
        userImage = (R.drawable.profpic)
        layoutDirection = LayoutDirection.Rtl
        containerColor = userColor
        rowAlignment = Alignment.CenterEnd
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
                Image(painter = painterResource(id = userImage), contentDescription = "aiProfile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .align(imgAlignment)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ){
                            isBoxVisible = !isBoxVisible
                        }
                ){
                    AnimatedVisibility(visible = isBoxVisible) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            contentAlignment = Alignment.CenterStart
                        ){

                            Text(
                                text = formattedTime,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Justify,
                                style = TextStyle(textDirection = TextDirection.Content)
                            )
                        }
                    }
                    Card (elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 20.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor,
                        )
                    ) {
                        Text(
                            text = message,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(10.dp),
                            textAlign = TextAlign.Justify,
                            style = TextStyle(textDirection = TextDirection.Content)
                        )
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
