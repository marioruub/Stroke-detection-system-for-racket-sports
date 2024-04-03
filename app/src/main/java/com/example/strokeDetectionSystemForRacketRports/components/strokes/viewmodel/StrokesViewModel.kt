package com.example.strokeDetectionSystemForRacketRports.components.strokes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strokeDetectionSystemForRacketRports.components.strokes.classes.StrokesData
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Strokes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This viewModel class is used to connect the view related with strokes and the model.
 *
 */
@HiltViewModel
class StrokesViewModel @Inject constructor(
    private val strokesData: StrokesData
) : ViewModel() {
    val eventsNames = MutableLiveData<ArrayList<String>>()

    /**
     * Allows to save the strokes done in a specific event.
     *
     */
    fun saveStrokesInEvent(eventList: ArrayList<String>, index: Int, strokes: Strokes, durationTime: Long){
        viewModelScope.launch { strokesData.saveStrokesInEvent(eventList, index, strokes, durationTime) }
    }

    /**
     * Returns to the view a list with all the events names.
     *
     */
    fun getEventsNames(){
        viewModelScope.launch { eventsNames.postValue(strokesData.getEventsNames()) }
    }
}