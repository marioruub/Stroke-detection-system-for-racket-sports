package com.example.strokeDetectionSystemForRacketRports.components.authentication.classes

import android.content.Intent
import com.example.strokeDetectionSystemForRacketRports.data.AuthResponse
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.UserSignUp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

/**
 * This class contains the functionality related to the user sign up.
 *
 */
class SignUp @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService,
    private val userDataValidation: UserDataValidation
) {

    /**
     * Allows the user to sign up and returns if it is successful or not.
     *
     */
    suspend fun signUpUser(userSignUp: UserSignUp): Boolean{
        return if(userDataValidation.isValidEmail(userSignUp.email) &&
            userDataValidation.isValidBothPassword(userSignUp.password, userSignUp.confirmPassword) &&
            userDataValidation.isValidName(userSignUp.firstName, userSignUp.secondName)){
            when(authenticationService.signUpUser(userSignUp.email, userSignUp.password)){
                AuthResponse.ERROR -> false
                AuthResponse.SUCCES -> {
                    createUserTable(userSignUp.email, userSignUp.firstName, userSignUp.secondName)
                    true
                }
            }
        }
        else false
    }

    /**
     * Allows the user to sign up with Google and returns if it is successful or not.
     *
     */
    suspend fun signUpWithGoogle(data: Intent?): Boolean{
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java) ?: return false
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            return when(authenticationService.signUpWithGoogle(credential)){
                AuthResponse.ERROR -> false
                AuthResponse.SUCCES -> {
                    createUserTable(account.email.toString(), account.givenName.toString(), account.familyName.toString())
                    true
                }
            }
        }
        catch(e: ApiException){
            return false
        }
    }

    /**
     * Allows to save user information.
     *
     */
    private suspend fun createUserTable(email: String, firstName: String, secondName: String){
        var currentUser = authenticationService.getCurrentUser()
        currentUser?.let {
            val doc = userService.getUserData(authenticationService.getCurrentUser()?.uid.toString())
            userService.saveUserData(it.uid,
                email,
                firstName,
                secondName,
                doc.data?.get("gender").toString(),
                doc.data?.get("birthDate").toString(),
                doc.data?.get("weight").toString(),
                doc.data?.get("height").toString())
        }
    }

}