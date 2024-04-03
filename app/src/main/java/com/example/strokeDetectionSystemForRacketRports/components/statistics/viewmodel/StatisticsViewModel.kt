package com.example.strokeDetectionSystemForRacketRports.components.statistics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.strokeDetectionSystemForRacketRports.components.statistics.classes.StatisticsCreate
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This viewModel class is used to connect the views related with the statistics fragment and the model.
 *
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsCreate: StatisticsCreate
) : ViewModel() {
    val lineChart1 = MutableLiveData<ArrayList<Entry>>()
    val lineChart2 = MutableLiveData<ArrayList<Entry>>()
    val lineChart3 = MutableLiveData<ArrayList<Entry>?>()
    val lineChart3Message = MutableLiveData<Boolean>()

    /**
     * Returns a list with all the entries needed to be add in the event line chart.
     *
     */
    fun getFisrtLineChartData(){
        viewModelScope.launch { lineChart1.postValue(statisticsCreate.createEventStatistic()) }
    }

    /**
     * Returns a list with all the entries needed to be add in the strokes line chart.
     *
     */
    fun getSecondLineChartData(){
        viewModelScope.launch { lineChart2.postValue(statisticsCreate.createStrokesStatistic()) }
    }

    /**
     * Returns a list with all the entries needed to be add in the calories line chart, only if the user data needed is available to calculate the calories
     *
     */
    fun getThirdLineChartData(){
        viewModelScope.launch {
            var entries = statisticsCreate.createCaloriesStatistic()
            if(entries != null) lineChart3.postValue(entries)
            else lineChart3Message.postValue(true)
        }
    }
}