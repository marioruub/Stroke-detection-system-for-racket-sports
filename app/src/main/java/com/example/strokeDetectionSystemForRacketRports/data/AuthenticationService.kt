package com.example.strokeDetectionSystemForRacketRports.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * This class makes the requests to the Firebase Authtentication module.
 *
 */
class AuthenticationService @Inject constructor() { //Firebase Authentication
    /**
     * Allows to sign up a user to firebase.
     *
     */
    suspend fun signUpUser(email: String, password: String): AuthResponse = runCatching {
        FirebaseClient.auth.createUserWithEmailAndPassword(email, password).await()
    }.authResult()

    /**
     * Allows to log in a user to firebase.
     *
     */
    suspend fun logInUser(email: String, password: String): AuthResponse = runCatching {
        FirebaseClient.auth.signInWithEmailAndPassword(email, password).await()
    }.authResult()

    /**
     * Reply to user sign up and user log in.
     *
     */
    private fun Result<AuthResult>.authResult(): AuthResponse {
        return when(getOrNull()) {
            null -> AuthResponse.ERROR
            else -> AuthResponse.SUCCES
        }
    }

    /**
     * Allows to log out a user to firebase.
     *
     */
    fun logOutUser(){
        FirebaseClient.auth.signOut()
    }

    /**
     * Allows to sign up with Google to firebase.
     *
     */
    suspend fun signUpWithGoogle(credential: AuthCredential): AuthResponse{
        try{
            FirebaseClient.auth.signInWithCredential(credential).await()
        }
        catch (e: Exception){
            return AuthResponse.ERROR
        }
        return AuthResponse.SUCCES
    }

    /**
     * Allows to get the data from the user that is currently loggued in.
     *
     */
    fun getCurrentUser(): FirebaseUser?{
        return FirebaseClient.auth.currentUser
    }

    /**
     * Allows to delete the current user from firebase.
     *
     */
    suspend fun deleteUser(): AuthResponse{
        try{
            FirebaseClient.auth.currentUser?.delete()?.await()
        }
        catch(e: Exception){
            return AuthResponse.ERROR
        }
        return AuthResponse.SUCCES
    }

    /**
     * Allows to send an email to the current user to verify his/her account.
     *
     */
    suspend fun sendVerificationEmail(){
        FirebaseClient.auth.currentUser?.sendEmailVerification()?.await()
    }

    /**
     * Allow to constantly check if the email has been verified.
     * When checked, it is indicated.
     */
    val verifyEmail: Flow<Boolean> = flow {
        while (true) {
            val verified = emailIsVerified()
            emit(verified)
            delay(2000)
        }
    }

    /**
     * Allows to check if the email is verified.
     *
     */
    private suspend fun emailIsVerified(): Boolean {
        FirebaseClient.auth.currentUser?.reload()?.await()
        return FirebaseClient.auth.currentUser?.isEmailVerified ?: false
    }

    /**
     * Allows to send an email to the current user to change his/her password.
     *
     */
    suspend fun sendChangePasswordEmail(email: String): AuthResponse{
        try {
            FirebaseClient.auth.sendPasswordResetEmail(email).await()
        }
        catch (e: FirebaseAuthException){
            return AuthResponse.ERROR
        }
        return AuthResponse.SUCCES
    }
}