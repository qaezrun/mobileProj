package com.example.aichatassistant.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aichatassistant.FirebaseUtils
import com.example.aichatassistant.R
import com.example.aichatassistant.api.ClearHist
import com.example.aichatassistant.api.deleteHistory
import com.example.aichatassistant.ui.theme.AiChatAssistantTheme
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatAssistant(fireViewModel: FirebaseModel) {
    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val message = remember {MessageModel()}
    val modal = remember{ModalModel()}
    message.addMessage(Message("What can I help you today?", 0,true))
    AiChatAssistantTheme {
        Scaffold(
            topBar = {
                MainBar(modal,fireViewModel)
            },
            bottomBar = {
                ChatBox(message)
            },
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
                if (modal.getShow) {
                    MyModal(
                        onDismissRequest = { modal.toggleModal() },
                        onConfirmation = { whatModalCanDo(message, modal.getTodo) },
                        header = modal.getHeader,
                        message = modal.getMessage,
                        withCancelBtn = modal.getCancel
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth(),
                    state = lazyColumnListState
                ) {
                    coroutineScope.launch {
                        if (message.getMessageList.size > 4) {
                            lazyColumnListState.animateScrollToItem(message.getMessageList.lastIndex)
                        }
                    }
                    items(message.getMessageList) { item ->
                        Response(item.from, item.message, item.sentStat)
                    }
                }
            }
        }
    }
}


@Composable
fun MainBar(modal: ModalModel, fireViewModel: FirebaseModel){
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
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 10.dp)
                        .clickable {
                            fireViewModel.updateFirebaseUser(null)// Logout User
                        }
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ){
                OutlinedButton(
                    onClick = {
                        modal.setModalValues("Are you sure?","This action will permanently delete all existing conversation, these messages will not be recoverable after this action.",0,true)
                        modal.toggleModal()
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



@OptIn(DelicateCoroutinesApi::class)
fun whatModalCanDo(message:MessageModel, whatTodo: Int){
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    when(whatTodo){
        0 -> {
            GlobalScope.launch(handler){
                val response: ClearHist = deleteHistory()
                println("${response.status } && ${response.message}")
            }
            message.clearRecentMessages()
            message.addMessage(Message("What can I help you today?", 0,true))
        }
    }
}