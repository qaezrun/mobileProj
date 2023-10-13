package com.example.aichatassistant.components

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.aichatassistant.FirebaseUtils
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

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
class ModalModel: ViewModel(){
    private var header = mutableStateOf("")
    private var message = mutableStateOf("")
    private var toDo = mutableIntStateOf(0)
    private var withCancelBtn = mutableStateOf(true)
    private var showModal = mutableStateOf(false)

    fun setModalValues(headMessage:String, modalMessage:String, modalFunc:Int, withCnlBtn: Boolean){
        header.value = headMessage
        message.value = modalMessage
        toDo.value = modalFunc
        withCancelBtn.value = withCnlBtn
    }
    fun toggleModal(){
        showModal.value = !showModal.value
    }

    val getHeader: String
        get() = header.value

    val getMessage:String
        get() = message.value

    val getTodo:Int
        get() = toDo.value

    val getShow: Boolean
        get() = showModal.value

    val getCancel: Boolean
        get() = showModal.value
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