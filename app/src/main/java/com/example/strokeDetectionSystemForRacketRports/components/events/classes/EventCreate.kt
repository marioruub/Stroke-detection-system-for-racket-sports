package com.example.strokeDetectionSystemForRacketRports.components.events.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventCreateInfo
import javax.inject.Inject

/**
 * This class contains the functionality related to creating an user event.
 *
 */
class EventCreate @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService
) {
    /**
     * Allows to create a match event.
     *
     */
    suspend fun createMatch(eventInfo: EventCreateInfo){
        val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventInfo.name

        val queryDocuments = userService.getEvents().documents
        var eventAlreadyExist = false

        for(document in queryDocuments){
            if(document.id == event)
                eventAlreadyExist = true
        }
        if(!eventAlreadyExist)
            userService.createMatch(event, eventInfo.place, eventInfo.date, eventInfo.time)
    }

    /**
     * Allows to create a training event.
     *
     */
    suspend fun createTraining(eventInfo: EventCreateInfo){
        val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventInfo.name

        val queryDocuments = userService.getEvents().documents
        var eventAlreadyExist = false

        for(document in queryDocuments){
            if(document.id == event)
                eventAlreadyExist = true
        }
        if(!eventAlreadyExist)
            userService.createTraining(event, eventInfo.place, eventInfo.date, eventInfo.time)
    }

    /**
     * Allows to create an other event.
     *
     */
    suspend fun createOtherEvent(eventInfo: EventCreateInfo){
        val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventInfo.name

        val queryDocuments = userService.getEvents().documents
        var eventAlreadyExist = false

        for(document in queryDocuments){
            if(document.id == event)
                eventAlreadyExist = true
        }
        if(!eventAlreadyExist)
            userService.createOtherEvent(event, eventInfo.place, eventInfo.date, eventInfo.time,)
    }

    /**
     * Allows to get all the events names.
     *
     */
    suspend fun getEventNames(): ArrayList<String>{
        val queryDocuments = userService.getEvents().documents
        var uidUser = authenticationService.getCurrentUser()?.uid.toString()
        var documentsNames = arrayListOf<String>()
        lateinit var tokens: List<String>

        for(document in queryDocuments){
            tokens = document.id.split("\\+".toRegex())
            if(tokens[0].trim().equals(uidUser)){
                documentsNames.add(tokens[1])
            }
        }

        return documentsNames
    }

    /**
     * Allows to delete an event.
     *
     */
    suspend fun deleteEvent(eventName: String): Boolean{
        val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventName
        userService.deleteEvent(event)
        return true
    }
}