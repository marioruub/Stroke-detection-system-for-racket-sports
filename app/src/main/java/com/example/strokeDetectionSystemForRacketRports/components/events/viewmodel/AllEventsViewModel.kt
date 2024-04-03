package com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strokeDetectionSystemForRacketRports.components.events.classes.EventCreate
import com.example.strokeDetectionSystemForRacketRports.components.events.classes.EventHome
import com.example.strokeDetectionSystemForRacketRports.components.events.classes.EventResult
import com.example.strokeDetectionSystemForRacketRports.components.events.classes.EventStrokes
import com.example.strokeDetectionSystemForRacketRports.components.events.model.*
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Strokes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This viewModel class is used to connect the views related with all types of events and the model.
 *
 */
@HiltViewModel
class AllEventsViewModel @Inject constructor(
    private val eventHome: EventHome,
    private val eventCreate: EventCreate,
    private val eventInfo: com.example.strokeDetectionSystemForRacketRports.components.events.classes.EventInfo,
    private val eventResult: EventResult,
    private val eventStrokes: EventStrokes
) : ViewModel() {
    val matchResults = MutableLiveData<MutableList<String>?>()
    val errorMessage1 = MutableLiveData<Boolean>()
    val errorMessage2 = MutableLiveData<Boolean>()
    val playerName = MutableLiveData<String>()
    val matchInfo = MutableLiveData<MatchInfo>()
    val strokes = MutableLiveData<Strokes>()

    val eventsNamesMatch = MutableLiveData<ArrayList<String>>()
    val eventsNamesTraining = MutableLiveData<ArrayList<String>>()
    val eventsNamesOtherEvent = MutableLiveData<ArrayList<String>>()
    val infoRecyclerView = MutableLiveData<ArrayList<EventInfoRecyclerView>>()
    val deleteMatch = MutableLiveData<Boolean>()
    val otherEventInfo = MutableLiveData<EventInfo>()

    /**
     * Returns to the view a list with all the events ordered by date and time.
     *
     */
    fun addEventsToRecyclerView(){
        viewModelScope.launch { infoRecyclerView.postValue(eventHome.addEventToList()) }
    }

    /**
     * Allows to create a match event.
     *
     */
    fun createMatch(eventInfo: EventCreateInfo){
        viewModelScope.launch { eventCreate.createMatch(eventInfo) }
    }

    /**
     * Allows to create a training event.
     *
     */
    fun createTraining(eventInfo: EventCreateInfo){
        viewModelScope.launch { eventCreate.createTraining(eventInfo) }
    }

    /**
     * Allows to create an other event.
     *
     */
    fun createOtherEvent(eventInfo: EventCreateInfo){
        viewModelScope.launch { eventCreate.createOtherEvent(eventInfo) }
    }

    /**
     * Returns to the view a list with all the matches events names.
     *
     */
    fun getEventsNamesMatch(){
        viewModelScope.launch { eventsNamesMatch.postValue(eventCreate.getEventNames()) }
    }

    /**
     * Returns to the view a list with all the trainings events names.
     *
     */
    fun getEventsNamesTraining(){
        viewModelScope.launch { eventsNamesTraining.postValue(eventCreate.getEventNames()) }
    }

    /**
     * Returns to the view a list with all the other events names.
     *
     */
    fun getEventsNamesOtherEvent(){
        viewModelScope.launch { eventsNamesOtherEvent.postValue(eventCreate.getEventNames()) }
    }

    /**
     * Returns to the view whether an event was successfully deleted or not.
     *
     */
    fun deleteEvent(eventName: String){
        viewModelScope.launch { deleteMatch.postValue(eventCreate.deleteEvent(eventName)) }
    }

    /**
     * Returns to the view the information of a match or training event.
     *
     */
    fun addMatchAndTrainingInfoToFields(eventName: String){
        viewModelScope.launch { matchInfo.postValue(eventInfo.addMatchAndTrainingInfoToFields(eventName)) }
    }

    /**
     * Returns to the view the information of an other event.
     *
     */
    fun addOtherEventInfoToFields(eventName: String){
        viewModelScope.launch { otherEventInfo.postValue(eventInfo.addOtherEventInfoToFields(eventName)) }
    }

    /**
     * Returns to the view whether a match or training were successfully saved or not.
     *
     */
    fun saveMatchAndTrainingInfo(info: MatchInfo, eventName: String){
        viewModelScope.launch { errorMessage1.postValue(eventInfo.saveMatchAndTrainingInfo(info, eventName)) }
    }

    /**
     * Returns to the view whether an other event was successfully saved or not.
     *
     */
    fun saveOtherEventInfo(info: EventInfo, eventName: String){
        viewModelScope.launch { errorMessage2.postValue(eventInfo.saveOtherEventInfo(info, eventName)) }
    }

    /**
     * Returns to the view a list with the results of a match event.
     *
     */
    fun addMatchResultsToFields(matchName: String){
        viewModelScope.launch { matchResults.postValue(eventResult.addMatchResultsToFields(matchName)) }
    }

    /**
     * Returns to the view whether the results of a match event were successfully saved or not.
     * If the results are successfully saved, they are returned to the view as well.
     */
    fun saveMatchResult(result: MatchResult, matchName: String){
        viewModelScope.launch {
            val result = eventResult.saveMatchResult(result, matchName)
            if(result != null){
                matchResults.postValue(result)
                errorMessage2.postValue(true)
            }
            else errorMessage2.postValue(false)
        }
    }

    /**
     * Returns to the view the opponent player name.
     *
     */
    fun addPlayerNameToField(){
        viewModelScope.launch { playerName.postValue(eventResult.addPlayerNameToField()) }
    }

    /**
     * Returns to the view all the strokes of an event.
     *
     */
    fun addStrokesToPieChart(eventName: String){
        viewModelScope.launch { strokes.postValue(eventStrokes.addStrokesToPieChart(eventName)) }
    }
}