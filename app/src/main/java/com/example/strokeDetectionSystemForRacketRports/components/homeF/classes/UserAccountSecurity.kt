package com.example.strokeDetectionSystemForRacketRports.components.homeF.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthResponse
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import javax.inject.Inject

/**
 * This class contains the functionality related with the security of the user account.
 *
 */
class UserAccountSecurity @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService,
    private val userDataSettingsValidation: UserDataSettingsValidation
) {
    /**
     * Allows to delete the user account.
     *
     */
    suspend fun deleteUserAccount(){
        userService.deleteUserData(authenticationService.getCurrentUser()?.uid.toString())
        authenticationService.deleteUser()
    }

    /**
     * Allows to delete all the events from a user.
     *
     */
    suspend fun deleteUserEvents(){
        val queryDocuments = userService.getEvents().documents
        var uidUser = authenticationService.getCurrentUser()?.uid.toString()
        lateinit var tokens: List<String>

        for(document in queryDocuments){
            tokens = document.id.split("\\+".toRegex())
            if(tokens[0].trim().equals(uidUser)){
                val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + tokens[1]
                userService.deleteUserEvents(event)
            }
        }
    }

    /**
     * Allows to send an email to the user email address for changing their password.
     *
     */
    suspend fun sendChangePasswordEmail(email: String): Boolean{
        return if(userDataSettingsValidation.isValidUserEmail(email)){
            when(authenticationService.sendChangePasswordEmail(email)){
                AuthResponse.ERROR -> false
                AuthResponse.SUCCES -> true
            }
        } else false
    }
}