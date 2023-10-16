package com.example.aichatassistant.components

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aichatassistant.FirebaseUtils
import com.example.aichatassistant.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    fireViewModel: FirebaseModel,
    modal: ModalModel,
    loading: LoadingSpinner
) {
    /*
        launcher is used for google sign in. Line 223
        fireViewModel is used on email/password sign in. Line 175

        if you see FirebaseUtils.firebaseAuth, it's just firebaseAuth instance
        you can look at it on FirebaseUtils
    */
    val token = stringResource(R.string.web_client_id)
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showSignUp by remember { mutableStateOf(false) }

    fun notEmpty(): Boolean = email.trim().isNotEmpty() && password.trim().isNotEmpty()
    if(showSignUp){
        SignUpFormPopUp(
            onDismissRequest = {
                showSignUp = false
            },
            onConfirmation = {
                showSignUp = false
                val a = listOfModal[3]
                modal.setModalValues(a.header,a.message,a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                modal.toggleModal()
            },
            modal,
            loading
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
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
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "I've been waiting for you",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 17.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurface
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
                        label = {Text(text="Email", style = MaterialTheme.typography.labelSmall, color = Color.Black)},
                        placeholder = {Text(text = "enter email...", style = MaterialTheme.typography.labelSmall)},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                            .height(60.dp),
                        textStyle = MaterialTheme.typography.labelSmall,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {Text(text = "Password", style = MaterialTheme.typography.labelSmall, color = Color.Black)},
                        placeholder = {Text(text = "enter password..", style = MaterialTheme.typography.labelSmall)},
                        singleLine = true,
                        textStyle = MaterialTheme.typography.labelSmall,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface
                        ),
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
                                        contentDescription = "hide_password",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { showPassword = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.VisibilityOff,
                                        contentDescription = "hide_password",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                            .height(60.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if(notEmpty()){
                                loading.toggleLoading()
                                FirebaseUtils.firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                                    .addOnCompleteListener{task->
                                        loading.toggleLoading()
                                        if(task.isSuccessful){
                                            fireViewModel.updateFirebaseUser(task.result)
                                        }else{
                                            val a = listOfModal[0]
                                            when (task.exception) {
                                                is FirebaseAuthInvalidCredentialsException -> {
                                                    // Prompt the user with a message like "Invalid email or password."
                                                    modal.setModalValues("Invalid email or password","The email or password you entered is incorrect, take your time we are more than willing to wait!",a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                                }
                                                is FirebaseAuthRecentLoginRequiredException -> {
                                                    // Prompt the user with a message like "You recently changed your password. Please sign in again using the new password."
                                                    modal.setModalValues("Do you remember?","You recently change your password, try signing in with your new password.",a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                                }
                                                is FirebaseAuthInvalidUserException -> {
                                                    // Prompt the user with a message like "The user has been disabled."
                                                    modal.setModalValues("This account is disabled","You might have tried signing in multiple times with wrong login credentials, you may try again later.",a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                                }
                                                else -> {
                                                    // Prompt the user with a generic error message.
                                                    modal.setModalValues("Login failed","Please make sure this account your trying to login is existing or the inputted credentials are correct.",a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                                }
                                            }
                                            modal.toggleModal()
                                        }
                                    }
                            }else{
                                val a = listOfModal[1]
                                modal.setModalValues(a.header,a.message,a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                modal.toggleModal()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Login", style = MaterialTheme.typography.titleLarge, fontSize = 16.sp)
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
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "SIGN UP",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 13.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable { showSignUp = true }
                        )
                        Text(
                            text = " or sign up using:" ,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
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
                        Icon(painter = painterResource(R.drawable.ic_google_logo), contentDescription = "",tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

class MyCustomException(message: String) : Exception(message)
@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit,
    loading: LoadingSpinner
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                loading.toggleLoading()
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                val user = hashMapOf(
                    "email" to authResult.user?.email,
                    "id" to authResult.user?.uid
                )
                FirebaseUtils.firebaseFireStore.collection("user").document(authResult.user?.uid.toString()).set(user)
                    .addOnSuccessListener {
                        loading.toggleLoading()
                        onAuthComplete(authResult)
                    }.addOnFailureListener {
                        loading.toggleLoading()
                        throw MyCustomException("Something went wrong")
                    }
            }
        } catch (e: ApiException) {
            loading.toggleLoading()
            onAuthError(e)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpFormPopUp(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modal: ModalModel,
    loading: LoadingSpinner
){
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
            colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.primary)
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
                                .size(23.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "We only ask for your email and a strong password.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {email = it},
                    label = {Text(text="Email", style = MaterialTheme.typography.labelSmall, color = Color.Black)},
                    placeholder = {Text(text = "enter email..", style = MaterialTheme.typography.labelSmall)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(60.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelSmall,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {Text(text = "Password", style = MaterialTheme.typography.labelSmall, color = Color.Black)},
                    placeholder = {Text(text = "enter password..", style = MaterialTheme.typography.labelSmall)},
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelSmall,
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
                                    contentDescription = "hide_password",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showPassword = true }) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = "hide_password",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(60.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                OutlinedTextField(
                    value = password2,
                    onValueChange = { password2 = it },
                    label = {Text(text = "Confirm Password", style = MaterialTheme.typography.labelSmall, color = Color.Black)},
                    placeholder = {Text(text = "enter password..", style = MaterialTheme.typography.labelSmall)},
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelSmall,
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
                                    contentDescription = "hide_password",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showPassword2 = true }) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = "hide_password",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(60.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = {
                         if(identicalPassword()){
                             loading.toggleLoading()
                             FirebaseUtils.firebaseAuth.createUserWithEmailAndPassword(email.trim(),password2.trim())
                                 .addOnCompleteListener{ task ->
                                     loading.toggleLoading()
                                     if(task.isSuccessful){
                                         val user = hashMapOf(
                                             "email" to task.result.user?.email,
                                             "id" to task.result.user?.uid
                                         )
                                         FirebaseUtils.firebaseFireStore.collection("user").document(task.result.user?.uid.toString()).set(user)
                                             .addOnSuccessListener {
                                                 onConfirmation()
                                             }.addOnFailureListener {
                                                 val a = listOfModal[2] // located at model
                                                 modal.setModalValues(a.header,it.message.toString(),a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                                 modal.toggleModal()
                                             }
                                     }else{
                                         val a = listOfModal[2] // located at model
                                         when (task.exception) {
                                             is FirebaseAuthUserCollisionException -> {
                                                 // Prompt the user with a message like "An account already exists with this email address."
                                                 modal.setModalValues("Email already exist!","An account already exists with this email address, use other email address.",a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                             }
                                             is FirebaseAuthWeakPasswordException -> {
                                                 // Prompt the user with a message like "Your password is too weak. Please choose a stronger password."
                                                 modal.setModalValues("Weak password!","Your password is too weak, Please choose a stronger password.",a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                             }
                                             is FirebaseAuthEmailException -> {
                                                 // Prompt the user with a message like "The email address is invalid."
                                                 modal.setModalValues("Invalid email address!","Make sure your email address is valid, please double check.",a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                             }
                                             else -> {
                                                 // Prompt the user with a generic error message.
                                                 modal.setModalValues("Unable to create account","Please make sure to fill up all the required fields.",a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                                             }
                                         }
                                         modal.toggleModal()
                                     }
                                 }
                         }else{
                             val a = listOfModal[2] // located at model
                             modal.setModalValues(a.header,error,a.btnOneName,a.btnTwoName,a.twoBtn,a.whatModalCanDo)
                             modal.toggleModal()
                         }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Create Account", style = MaterialTheme.typography.titleLarge, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}