package com.example.aichatassistant.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatAssistant(
    fireViewModel: FirebaseModel,
    modal: ModalModel,
    message: MessageModel,
    loading: LoadingSpinner
) {
    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
            if(message.getMessageList.isEmpty()){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ){
                    Column (
                        modifier = Modifier
                            .align(Alignment.Center)
                    ){
                        Text(text = "Ask me anything!", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                    }
                }
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


@Composable
fun MainBar(modal: ModalModel, fireViewModel: FirebaseModel){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.primary)
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            fireViewModel.updateFirebaseUser(null)// Logout User
                        },
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ){
                OutlinedButton(
                    onClick = {
                        val a = listOfModal[4] // located at model
                        modal.setModalValues(a.header,a.message,a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                        modal.toggleModal()
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    contentPadding = PaddingValues(start = 15.dp, top = 0.dp, bottom = 0.dp, end = 15.dp)
                ) {
                    Text(
                        text = "Clear chat",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}