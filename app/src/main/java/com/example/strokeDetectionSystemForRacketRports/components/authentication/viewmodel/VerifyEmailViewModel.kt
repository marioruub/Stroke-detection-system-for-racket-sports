package com.example.strokeDetectionSystemForRacketRports.components.authentication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.components.authentication.classes.VerifyEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This viewModel class is used to connect the verifyEmail activity with the model.
 *
 */
@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val verifyEmail: VerifyEmail,
    private val authenticationService: AuthenticationService
) : ViewModel() {
    val showButton = MutableLiveData<Boolean>()

    init{
        viewModelScope.launch {
            verifyEmail.sendUserVerificationEmail()
            authenticationService.verifyEmail.collect {
                showButton.postValue(it)
            }
        }
    }

    /**
     * Returns to the view whether the user account has been succsessfully deleted or not.
     *
     */
    fun deleteNonVerifiedAccounts(){
        viewModelScope.launch { verifyEmail.deleteNonVerifiedAccounts() }
    }
}