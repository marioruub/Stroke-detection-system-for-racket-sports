package com.example.strokeDetectionSystemForRacketRports.components.homeF.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strokeDetectionSystemForRacketRports.components.events.classes.EventInfo
import com.example.strokeDetectionSystemForRacketRports.components.homeF.classes.ProfileImage
import com.example.strokeDetectionSystemForRacketRports.components.homeF.classes.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * This viewModel class is used to connect the views related with the home fragment and the model.
 *
 */
@HiltViewModel
class HomeFViewModel @Inject constructor(
    private val profileImage: ProfileImage,
    private val userData: UserData,
    private val eventInfo: EventInfo
) : ViewModel() {
    val userImageUrl = MutableLiveData<Uri>()
    val userTopData = MutableLiveData<ArrayList<String>>()
    val matchesHomePage = MutableLiveData<ArrayList<ArrayList<String>>>()
    val trainingsHomePage = MutableLiveData<ArrayList<ArrayList<String>>>()
    val otherEventsHomePage = MutableLiveData<ArrayList<ArrayList<String>>>()

    /**
     * Returns to the uri from the user profile image.
     *
     */
    fun getUserProfileImage(){
        viewModelScope.launch { userImageUrl.postValue(profileImage.getUserProfileImage()) }
    }

    /**
     * Returns a list with the user data shown in the home fragment.
     *
     */
    fun getUserInfo(){
        viewModelScope.launch { userTopData.postValue(userData.getUserHomeInfo()) }
    }

    /**
     * Returns a list with the names of the matches events in the next seven days.
     *
     */
    fun getMatchesForNextSevenDays(){
        viewModelScope.launch { matchesHomePage.postValue(eventInfo.getMatchesForNextSevenDays()) }
    }

    /**
     * Returns a list with the names of the trainings events in the next seven days.
     *
     */
    fun getTrainingsForNextSevenDays(){
        viewModelScope.launch { trainingsHomePage.postValue(eventInfo.getTrainingsForNextSevenDays()) }
    }

    /**
     * Returns a list with the names of the other events in the next seven days.
     *
     */
    fun getOtherEventsForNextSevenDays(){
        viewModelScope.launch { otherEventsHomePage.postValue(eventInfo.getOtherEventsForNextSevenDays()) }
    }
}