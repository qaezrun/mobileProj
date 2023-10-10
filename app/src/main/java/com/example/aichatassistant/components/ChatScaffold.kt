package com.example.aichatassistant.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aichatassistant.R
import com.example.aichatassistant.api.ClearHist
import com.example.aichatassistant.api.deleteHistory
import com.example.aichatassistant.ui.theme.AiChatAssistantTheme
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatAssistant(){
    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val message = remember { MessageModel() }
    message.addMessage(Message("What can I help you today?", 0,true))
    AiChatAssistantTheme {
        Scaffold (
            topBar = {
                MainBar(message)
            },
            bottomBar = {
                ChatBox(message)
            },
            modifier = Modifier.fillMaxSize()
        ){padding ->
            Surface (
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface 
            ){
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth(),
                    state = lazyColumnListState
                ){
                    coroutineScope.launch {
                        if(message.getMessageList.size > 4){
                            lazyColumnListState.animateScrollToItem(message.getMessageList.lastIndex)
                        }
                    }
                    items(message.getMessageList) {item ->
                        Response(item.from,item.message,item.sentStat)
                    }
                }
            }
        }
    }
}


@Composable
fun MainBar(message: MessageModel){
    val coroutineScope = rememberCoroutineScope()
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    var showModal by remember {mutableStateOf(false)}
    var headerModal by remember {mutableStateOf("")}
    var messageModal by remember {mutableStateOf("")}
    var cancelBtnModal by remember {mutableStateOf(true)}
    if(showModal){
        MyModal(
            onDismissRequest = { showModal = false},
            onConfirmation = {
                coroutineScope.launch(handler){
                    val response: ClearHist = deleteHistory()
                    println("${response.status } && ${response.message}")
                }
                message.clearRecentMessages()
                message.addMessage(Message("What can I help you today?", 0,true))
            },
            header = headerModal,
            message = messageModal,
            withCancelBtn = cancelBtnModal
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.onTertiary)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Box(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ){
                Text(
                    text = "Chat Bot",
                    fontSize = 17.sp,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ){
                OutlinedButton(
                    onClick = {
                        headerModal = "Are you sure?"
                        messageModal = "This action will permanently delete all existing conversation, these messages will not be recoverable after this action."
                        showModal = true
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color =  Color(0xFFAAE9E6)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    contentPadding = PaddingValues(start = 15.dp, top = 0.dp, bottom = 0.dp, end = 15.dp)
                ) {
                    Text(text = "Clear chat")
                }
            }
        }
    }
}

@Composable
fun MyModal(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    header:String,
    message:String,
    withCancelBtn: Boolean
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor =  MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.phoenix),
                    contentDescription = "imageModal",
                    modifier = Modifier
                        .size(150.dp)
                )
                Box{
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
                    ){
                        Text(
                            text = header,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .align(Alignment.CenterHorizontally),
                            color = Color.Blue
                        )
                        Text(
                            text = message,
                            textAlign = TextAlign.Justify,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if(withCancelBtn){
                        TextButton(
                            onClick = { onDismissRequest() },
                        ) {
                            Text("Cancel")
                        }
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                    ) {
                        Text(text = "Proceed")
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun ShowTopBar(){
    MyModal(
        onDismissRequest = { /*TODO*/ },
        onConfirmation = { /*TODO*/ },
        header = "Are you sure?",
        message = "This action will permanently delete all existing conversation, these messages will not be recoverable after this action.",
        withCancelBtn = true
    )
}
