package com.example.strokeDetectionSystemForRacketRports.components.homeF.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.User
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.UserDataSettings
import com.example.strokeDetectionSystemForRacketRports.components.homeF.classes.ProfileImage
import com.example.strokeDetectionSystemForRacketRports.components.homeF.classes.UserAccountSecurity
import com.example.strokeDetectionSystemForRacketRports.components.homeF.classes.UserData
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This viewModel class is used to connect the views related with the settings fragment and the model.
 *
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userData: UserData,
    private val profileImage: ProfileImage,
    private val userAccountSecurity: UserAccountSecurity
) : ViewModel() {

    val errorMessage1 = MutableLiveData<Boolean>()
    val errorMessage2 = MutableLiveData<Boolean>()
    val userDataFields = MutableLiveData<User>()
    val userImage = MutableLiveData<Boolean>()
    val deleteUserImage = MutableLiveData<Boolean>()

    /**
     * Returns to the view the data of the current user.
     *
     */
    fun addUserDataToFields(){
        viewModelScope.launch { userDataFields.postValue(userData.addUserDataToFields()) }
    }

    /**
     * Returns to the view whether the user data was successfully saved or not.
     *
     */
    fun saveUserData(userDataSettings: UserDataSettings){
        viewModelScope.launch { errorMessage1.postValue(userData.saveUserData(userDataSettings)) }
    }

    /**
     * Returns to the view whether the user profile image was successfully saved or not.
     *
     */
    fun saveUserImage(bitmap: Bitmap){
        userImage.postValue(profileImage.saveUserProfileImage(bitmap))
    }

    /**
     * Returns to the view whether the user profile image was successfully deleted or not.
     *
     */
    fun deleteUserImage(){
        deleteUserImage.postValue(profileImage.deleteUserProfileImage())
    }

    /**
     * Allows to delete the account from the current user.
     *
     */
    fun deleteUserAccount(){
        viewModelScope.launch{ userAccountSecurity.deleteUserAccount() }
    }

    /**
     * Allows to delete all the events from the current user.
     *
     */
    fun deleteUserEvents(){
        viewModelScope.launch { userAccountSecurity.deleteUserEvents() }
    }

    /**
     * Returns to the view whether the user password was successfully changed or not.
     *
     */
    fun sendChangePasswordEmail(){
        viewModelScope.launch { errorMessage2.postValue(userAccountSecurity.sendChangePasswordEmail(authenticationService.getCurrentUser()?.email.toString())) }
    }

}