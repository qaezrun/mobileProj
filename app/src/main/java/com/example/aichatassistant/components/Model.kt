package com.example.aichatassistant.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import com.example.aichatassistant.FirebaseUtils
import com.example.aichatassistant.R
import com.example.aichatassistant.api.ClearHist
import com.example.aichatassistant.api.deleteHistory
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class Message(
    val message:String,
    val from: Int,
    var sentStat: Boolean
)

class MessageModel: ViewModel() {
    private val messageList = mutableStateListOf<Message>()
    fun addMessage(message: Message) {
        messageList.add(message)
    }
    val getMessageList: List<Message>
        get() = messageList
    fun toggleSentStat(messageIndex: Int) {
        messageList[messageIndex].sentStat = false
    }
    fun clearRecentMessages(){
        messageList.clear()
    }
}
class FirebaseModel: ViewModel(){
    /*
        This viewModel is done so that which ever way user wants to
        login even if its google or email/password provider you'll be able to go to
        main screen which is the chat.
    */
    private var user = mutableStateOf(FirebaseUtils.firebaseUser)//By default user is assigned with value from FirebaseUtils
    fun getUser(): FirebaseUser? {
        return user.value
    } //If user gets updated by updateFirebaseUser this means that user login to the app
    fun updateFirebaseUser(newFirebaseUser: AuthResult?) {
        if (newFirebaseUser != null) {
            user.value = newFirebaseUser.user
        }else{
            FirebaseUtils.firebaseAuth.signOut()
            user.value = null
        }
    }
}


//ViewModel Loading Spinner
class LoadingSpinner : ViewModel(){
    private var showLoading = mutableStateOf(false)
    fun toggleLoading(){
        showLoading.value = !showLoading.value
    }
    val getLoadingState:Boolean
        get() = showLoading.value
}


@Composable
fun ProgressIndicatorLoading(
    progressIndicatorSize: Dp,
    progressIndicatorColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
            }
        )
    )

    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                5.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        MaterialTheme.colorScheme.tertiary, // add background color first
                        progressIndicatorColor.copy(alpha = 0.1f),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = MaterialTheme.colorScheme.surface// Set background color
    )
}

@Composable
fun LoadingPop(){
    Dialog(
        onDismissRequest = {}
    ){
        Column(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
            ){
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    ProgressIndicatorLoading(
                        progressIndicatorSize = 60.dp,
                        progressIndicatorColor = MaterialTheme.colorScheme.secondary
                    )
                    // Gap between progress indicator and text
                    Spacer(modifier = Modifier.height(10.dp))

                    // Please wait text
                    Text(
                        text = "Please wait...",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}



//MY REUSABLE MODAL
data class ModalMessage(
    val header: String,
    val message: String,
    val btnOneName: String,
    val btnTwoName: String,
    val twoBtn: Boolean,
    val whatModalCanDo: Int
)
val listOfModal = listOf(
    /*Login Messages*/
    ModalMessage("Login Failed!","","","Ok",false,0),
    ModalMessage("Fill up the form!","Please make sure to fill up empty field before proceeding.","","Ok",false,0),
    ModalMessage("Account Creation Failed!","","","Ok",false,0),
    ModalMessage("Account Created!","Welcome human! you may now use the services i provide.","","Proceed",false,0),

    /*Clear Chat*/
    ModalMessage("Are you sure?","This action will permanently delete all existing conversation, these messages will not be recoverable after this action.","Delete","Cancel",true,1),
)

class ModalModel: ViewModel(){
    private var header = mutableStateOf("")
    private var message = mutableStateOf("")
    private var btnOne = mutableStateOf("")
    private var btnTwo = mutableStateOf("")
    private var twoBtn = mutableStateOf(false)
    private var toDo = mutableIntStateOf(0)
    private var showModal = mutableStateOf(false)

    fun setModalValues(headMessage:String, modalMessage:String, btnOneName:String, btnTwoName:String,hasTwoBtn:Boolean, modalFunc:Int){
        header.value = headMessage
        message.value = modalMessage
        btnOne.value = btnOneName
        btnTwo.value = btnTwoName
        twoBtn.value = hasTwoBtn
        toDo.value = modalFunc
    }
    fun toggleModal(){
        showModal.value = !showModal.value
    }

    val getHeader: String
        get() = header.value

    val getMessage:String
        get() = message.value

    val getBtnOneName:String
        get() = btnOne.value

    val getBtnTwoName:String
        get() = btnTwo.value

    val getTodo:Int
        get() = toDo.value

    val getShow: Boolean
        get() = showModal.value

    val getTwoBtn: Boolean
        get() = twoBtn.value
}


@Composable
fun MyModal(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    header:String,
    message:String,
    btnOne:String,
    btnTwo:String,
    twoBtn:Boolean,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor =  MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = message,
                            textAlign = TextAlign.Justify,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if(twoBtn){
                        TextButton(
                            onClick = {onConfirmation()},
                        ) {
                            Text(text = btnOne,style = MaterialTheme.typography.titleLarge, fontSize = 16.sp,color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    TextButton(
                        onClick = {onDismissRequest()},
                    ) {
                        Text(text = btnTwo,style = MaterialTheme.typography.titleLarge, fontSize = 16.sp,color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun whatModalCanDo(message: MessageModel, whatTodo: Int, modal: ModalModel){
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    when(whatTodo){
        0 -> {
            modal.toggleModal()
        }
        1 ->{
            GlobalScope.launch(handler){
                message.clearRecentMessages()
                message.addMessage(Message("What can I help you today?", 0,true))
                val response: ClearHist = deleteHistory()
                println("${response.status } && ${response.message}")
                modal.toggleModal()
            }
        }
        else ->{
            //do nothing
        }
    }
}