package com.example.strokeDetectionSystemForRacketRports.components.homeA.viewmodel

import androidx.lifecycle.ViewModel
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService

/**
 * This viewModel class is used to connect the view related with home activity and the model.
 *
 */
class HomeAViewModel : ViewModel(){
    companion object{
        val authenticationService: AuthenticationService = AuthenticationService()
    }

    /**
     * Allows to log out an user account from the application.
     *
     */
    fun logOutUser(){
        authenticationService.logOutUser()
    }
}