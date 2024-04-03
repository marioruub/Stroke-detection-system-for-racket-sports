package com.example.strokeDetectionSystemForRacketRports.components.authentication.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import javax.inject.Inject

/**
 * This class contains the functionality related to the verification of the user's email.
 *
 */
class VerifyEmail @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService
) {

    /**
     * Allows to send an email to the current user to verify their account.
     *
     */
    suspend fun sendUserVerificationEmail(){
        authenticationService.sendVerificationEmail()
    }

    /**
     * Allows to delete the user accounts which where not verified.
     *
     */
    suspend fun deleteNonVerifiedAccounts(){
        //Eliminar el usuario de firebase firestore
        userService.deleteUserData(authenticationService.getCurrentUser()?.uid.toString())
        //Eliminar el usuario de firebase authentication
        authenticationService.deleteUser()
    }
}