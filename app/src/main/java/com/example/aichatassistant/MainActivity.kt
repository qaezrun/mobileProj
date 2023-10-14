package com.example.aichatassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.example.aichatassistant.components.AiChatAssistant
import com.example.aichatassistant.components.FirebaseModel
import com.example.aichatassistant.components.LoadingPop
import com.example.aichatassistant.components.LoadingSpinner
import com.example.aichatassistant.components.LoginForm
import com.example.aichatassistant.components.MessageModel
import com.example.aichatassistant.components.ModalModel
import com.example.aichatassistant.components.MyModal
import com.example.aichatassistant.components.rememberFirebaseAuthLauncher
import com.example.aichatassistant.components.whatModalCanDo
import com.example.aichatassistant.ui.theme.AiChatAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val fireViewModel = remember { FirebaseModel() }//Go to components/model.kt if you want clarification
            val modal = remember{ ModalModel()}
            val loading = remember{LoadingSpinner()}
            val message = remember { MessageModel() }
            val launcher = rememberFirebaseAuthLauncher(
                onAuthComplete = { result ->
                    fireViewModel.updateFirebaseUser(result)
                },
                onAuthError = {
                    fireViewModel.updateFirebaseUser(null)
                }
            )
            AiChatAssistantTheme {
                /*This condition checks if user is currently login or not*/
                if(fireViewModel.getUser() != null){ //Go to components/model and find FirebaseModel
                    AiChatAssistant(fireViewModel,modal,message,loading)
                }else{
                    LoginForm(launcher = launcher,fireViewModel,modal,loading)
                }

                /*Global Modal */
                if (modal.getShow) {
                    MyModal(
                        onDismissRequest = { modal.toggleModal() },
                        onConfirmation = { whatModalCanDo(message, modal.getTodo, modal) },
                        header = modal.getHeader,
                        message = modal.getMessage,
                        btnOne = modal.getBtnOneName,
                        btnTwo = modal.getBtnTwoName,
                        twoBtn = modal.getTwoBtn
                    )
                }

                /*Global Loading Spinner*/
                if(loading.getLoadingState){
                    LoadingPop()
                }
            }
        }
    }
}