package com.example.strokeDetectionSystemForRacketRports.components.strokes.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Strokes
import javax.inject.Inject

/**
 * This class contains the functionality related to the strokes.
 *
 */
class StrokesData @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService
) {
    /**
     * Allows to save the strokes done in an event.
     *
     */
    suspend fun saveStrokesInEvent(eventList: ArrayList<String>, index: Int, strokes: Strokes, durationTime: Long){
        val event = eventList.get(index)
        val uidUser = authenticationService.getCurrentUser()?.uid.toString()
        userService.saveStrokes(uidUser+"+"+event, strokes, durationTime.toString())
    }

    /**
     * Allows to get the events names.
     *
     */
    suspend fun getEventsNames(): ArrayList<String>{
        var uidUser = authenticationService.getCurrentUser()?.uid.toString()
        var documentsNames = arrayListOf<String>()

        val queryMatches = userService.getMatches().documents
        lateinit var tokens1: List<String>
        for(document in queryMatches){
            tokens1 = document.id.split("\\+".toRegex())
            if(tokens1[0].trim().equals(uidUser)){
                documentsNames.add(tokens1[1])
            }
        }

        val queryTrainings = userService.getTrainings().documents
        lateinit var tokens2: List<String>
        for(document in queryTrainings){
            tokens2 = document.id.split("\\+".toRegex())
            if(tokens2[0].trim().equals(uidUser)){
                documentsNames.add(tokens2[1])
            }
        }

        return documentsNames
    }
}