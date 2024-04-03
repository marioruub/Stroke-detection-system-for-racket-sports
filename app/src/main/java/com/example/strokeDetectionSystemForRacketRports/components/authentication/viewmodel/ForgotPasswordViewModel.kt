package com.example.strokeDetectionSystemForRacketRports.components.authentication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strokeDetectionSystemForRacketRports.components.authentication.classes.ChangePassword
import com.example.strokeDetectionSystemForRacketRports.components.authentication.classes.ChangePasswordResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This viewModel class is used to connect the forgotPassword activity with the model.
 *
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val changePassword: ChangePassword
) : ViewModel(){

    val errorMessage = MutableLiveData<Boolean>()
    val emailErrorMessage = MutableLiveData<Boolean>()

    /**
     * Returns to the view whether the user has succsessfully changed their password or not.
     *
     */
    fun forgotPassword(email: String){
        viewModelScope.launch {
            when(changePassword.sendChangePasswordEmail(email)){
                ChangePasswordResponse.NOT_VALID_EMAIL -> emailErrorMessage.postValue(true)
                ChangePasswordResponse.ERROR -> errorMessage.postValue(false)
                ChangePasswordResponse.SUCCES -> errorMessage.postValue(true)
            }
        }
    }
}