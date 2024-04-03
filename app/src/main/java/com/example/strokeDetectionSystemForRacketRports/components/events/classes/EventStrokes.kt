package com.example.strokeDetectionSystemForRacketRports.components.events.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Strokes
import javax.inject.Inject

/**
 * This class contains the functionality related to the strokes of the events.
 *
 */
class EventStrokes @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService
) {
    /**
     * Allows to get all the strokes of an event.
     *
     */
    suspend fun addStrokesToPieChart(eventName: String): Strokes{
        //var strokes = Strokes(0, 0)
        var strokes = Strokes(0, 0, 0)
        val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventName

        val doc = userService.getEventInfo(event)
        if (doc.exists()){
            strokes = Strokes(
                doc.data?.get("forehand").toString().toInt(),
                doc.data?.get("backhand").toString().toInt(),
                doc.data?.get("serve").toString().toInt(),
            )
        }

        return strokes
    }
}