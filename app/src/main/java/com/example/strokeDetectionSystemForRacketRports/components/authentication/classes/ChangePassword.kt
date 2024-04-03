package com.example.strokeDetectionSystemForRacketRports.components.authentication.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthResponse
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import javax.inject.Inject

/**
 * This class contains the functionality related to changing the user's password.
 *
 */
class ChangePassword @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val dataValidation: UserDataValidation
) {

    /**
     * Allows the user to change their password.
     *
     */
    suspend fun sendChangePasswordEmail(email: String): ChangePasswordResponse {
        return if(dataValidation.isValidEmail(email)){
            when(authenticationService.sendChangePasswordEmail(email)){
                AuthResponse.ERROR -> ChangePasswordResponse.ERROR
                AuthResponse.SUCCES -> ChangePasswordResponse.SUCCES
            }
        } else ChangePasswordResponse.NOT_VALID_EMAIL
    }
}