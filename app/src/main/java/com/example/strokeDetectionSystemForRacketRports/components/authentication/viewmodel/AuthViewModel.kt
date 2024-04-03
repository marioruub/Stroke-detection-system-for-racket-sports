package com.example.strokeDetectionSystemForRacketRports.components.authentication.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.components.authentication.classes.LogIn
import com.example.strokeDetectionSystemForRacketRports.components.authentication.classes.SignUp
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.UserLogIn
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.UserSignUp
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This viewModel class is used to connect the views related with authentication and the model.
 *
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val logIn: LogIn,
    private val signUp: SignUp
) : ViewModel() {

    val signUpMessage = MutableLiveData<Boolean>()
    val logInMessage = MutableLiveData<Boolean>()
    val goToVerifiyEmail = MutableLiveData<Boolean>()
    val signUpWithGoogle = MutableLiveData<Boolean>()

    /**
     * Returns to the view whether the user sign up was successful or not.
     *
     */
    fun signUpUser(userSignUp: UserSignUp){
        viewModelScope.launch{
            if(signUp.signUpUser(userSignUp)){
                signUpMessage.postValue(true)
                goToVerifiyEmail.postValue(true)
            }
            else signUpMessage.postValue(false)
        }
    }

    /**
     * Returns to the view whether the user log in was successful or not.
     *
     */
    fun logInUser(userLogIn: UserLogIn){
        viewModelScope.launch{ logInMessage.postValue(logIn.logInUser(userLogIn)) }
    }

    /**
     * Returns to the view whether the user log in with Google was successful or not.
     *
     */
    fun logInWithGoogle(data: Intent?){
        viewModelScope.launch { signUpWithGoogle.postValue(logIn.logInWithGoogle(data)) }
    }

    /**
     * Returns to the view whether the user sign up with Google was successful or not.
     *
     */
    fun signUpWithGoogle(data: Intent?){
        viewModelScope.launch { signUpWithGoogle.postValue(signUp.signUpWithGoogle(data)) }
    }

    /**
     * Returns to the view the current user data.
     *
     */
    fun getCurrentUser(): FirebaseUser?{
        return authenticationService.getCurrentUser()
    }
}