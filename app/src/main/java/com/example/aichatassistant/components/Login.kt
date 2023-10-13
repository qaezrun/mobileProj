package com.example.aichatassistant.components
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.aichatassistant.FirebaseUtils
import com.example.aichatassistant.R
import com.example.aichatassistant.ui.theme.AiChatAssistantTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuthException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    fireViewModel: FirebaseModel
) {
    /*
        launcher is used for google sign in. Line 223
        fireViewModel is used on email/password sign in. Line 175

        if you see FirebaseUtils.firebaseauth, it's just firebaseauth instance
        you can look at it on FirebaseUtils
    */
    val token = stringResource(R.string.web_client_id)
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showSignUp by remember { mutableStateOf(false) }
    fun notEmpty(): Boolean = email.trim().isNotEmpty() && password.trim().isNotEmpty()

    AiChatAssistantTheme {
        if(showSignUp){
            SignUpFormPopUp { showSignUp = false }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                ){
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ){
                        Text(
                            text = "Welcome Human!",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "I've been waiting for you",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ){
                    Column{
                        OutlinedTextField(
                            value = email,
                            onValueChange = {email = it},
                            label = {Text(text="Email", style = MaterialTheme.typography.labelSmall)},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp)
                                .height(60.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {Text(text = "Password", style = MaterialTheme.typography.labelSmall)},
                            visualTransformation =  if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            trailingIcon = {
                                if (showPassword) {
                                    IconButton(onClick = { showPassword = false }) {
                                        Icon(
                                            imageVector = Icons.Filled.Visibility,
                                            contentDescription = "hide_password"
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = { showPassword = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.VisibilityOff,
                                            contentDescription = "hide_password"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp)
                                .height(60.dp)
                        )
                        Button(
                            onClick = {
                                if(notEmpty()){
                                    FirebaseUtils.firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                                        .addOnCompleteListener{task->
                                            if(task.isSuccessful){
                                                fireViewModel.updateFirebaseUser(task.result)
                                            }else{
                                                val err = task.exception?.message
                                                Toast.makeText(context,err.toString(),Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }else{
                                    Toast.makeText(context,"Fill up login form!",Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                        ) {
                            Text(text = "Login", style = MaterialTheme.typography.labelSmall,modifier = Modifier.padding(6.dp))
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                ){
                    Column (
                        modifier = Modifier
                            .align(Alignment.Center)
                    ){
                        Row(
                            modifier = Modifier
                                .padding(5.dp)
                        ){
                            Text(
                                text = "Don't have an account yet? ",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "SIGN UP",
                                color = Color.Blue,
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .clickable { showSignUp = true }
                            )
                            Text(
                                text = " or sign up using:" ,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(
                            onClick = {
                                val gso =
                                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(token)
                                        .requestEmail()
                                        .build()
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                launcher.launch(googleSignInClient.signInIntent)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Icon(painter = painterResource(R.drawable.ic_google_logo), contentDescription = "")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpFormPopUp(
    onDismissRequest: () -> Unit
){
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var password2 by rememberSaveable { mutableStateOf("") }
    var showPassword2 by remember { mutableStateOf(value = false) }
    var showPassword by remember { mutableStateOf(value = false) }
    var error by rememberSaveable{ mutableStateOf("") }

    fun notEmpty(): Boolean = email.trim().isNotEmpty() && password.trim().isNotEmpty() && password2.trim().isNotEmpty()
    fun identicalPassword(): Boolean {
        var identical = false
        if(notEmpty()&& password.trim() == password2.trim()){
            identical = true
        }else if(!notEmpty()){
            error = "Please fill up the form"
        }else{
            error = "Password does not match!"
        }
        return identical
    }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.onTertiary)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(
                        onClick = { onDismissRequest()},
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close",
                            modifier = Modifier
                                .padding(0.dp)
                                .size(17.dp)
                        )
                    }
                }
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "We only ask for your email and a strong password.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {email = it},
                    label = {Text(text="Email", style = MaterialTheme.typography.labelSmall)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(60.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {Text(text = "Password", style = MaterialTheme.typography.labelSmall)},
                    visualTransformation =  if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        if (showPassword) {
                            IconButton(onClick = { showPassword = false }) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "hide_password"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showPassword = true }) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = "hide_password"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(60.dp)
                )
                OutlinedTextField(
                    value = password2,
                    onValueChange = { password2 = it },
                    label = {Text(text = "Confirm Password", style = MaterialTheme.typography.labelSmall)},
                    visualTransformation =  if (showPassword2) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        if (showPassword2) {
                            IconButton(onClick = { showPassword2 = false }) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "hide_password"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showPassword2 = true }) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = "hide_password"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(60.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = {
                         if(identicalPassword()){
                             FirebaseUtils.firebaseAuth.createUserWithEmailAndPassword(email.trim(),password2.trim())
                                 .addOnCompleteListener{ task ->
                                     if(task.isSuccessful){
                                         Toast.makeText(context,"user created",Toast.LENGTH_SHORT).show()
                                         onDismissRequest()
                                     }else{
                                         val err = task.exception?.message
                                         Toast.makeText(context,err.toString(),Toast.LENGTH_SHORT).show()
                                     }
                                 }
                         }else{
                             Toast.makeText(context,error,Toast.LENGTH_SHORT).show()
                         }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                ) {
                    Text(text = "Create Account", style = MaterialTheme.typography.labelSmall,modifier = Modifier.padding(6.dp))
                }
            }
        }
    }
}
