package com.example.strokeDetectionSystemForRacketRports.components.events.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.example.strokeDetectionSystemForRacketRports.components.events.model.MatchResult
import javax.inject.Inject

/**
 * This class contains the functionality related to the results of the events.
 *
 */
class EventResult @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService,
    private val eventDataValidation: EventDataValidation
)  {
    /**
     * Allows to get an event match results.
     *
     */
    suspend fun addMatchResultsToFields(matchName: String): MutableList<String>?{
        var matchResults = mutableListOf<String>()
        val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + matchName

        val doc = userService.getEventInfo(event)
        if (doc.exists()){
            matchResults = doc.data?.get("setPoints") as MutableList<String>
        }

        return matchResults
    }

    /**
     * Allows to save an event match results.
     *
     */
    suspend fun saveMatchResult(result: MatchResult, matchName: String): MutableList<String>? {
        var resultList = mutableListOf(result.playerName,
            result.firstSet1,
            result.firstSet2,
            result.secondSet1,
            result.secondSet2,
            result.thirdSet1,
            result.thirdSet2,
            result.fourthSet1,
            result.fourthSet2,
            result.fifthSet1,
            result.fifthSet2)
        var updatedResult = mutableListOf<String>()

        for(result in resultList){
            if(result == "")
                updatedResult.add("0")
            else
                updatedResult.add(result)
        }

        return if(eventDataValidation.isValidResult(updatedResult)) {
            val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + matchName
            userService.saveMatchResult(event, updatedResult)
            updatedResult
        } else null
    }

    /**
     * Allows to get the opponent player name.
     *
     */
    suspend fun addPlayerNameToField(): String{
        var name = ""
        var secondName = ""

        val doc = userService.getUserData(authenticationService.getCurrentUser()?.uid.toString())
        if (doc.exists()){
            if(doc.data?.get("secondName").toString() != "null")
                secondName = doc.data?.get("secondName").toString()
            name = doc.data?.get("firstName").toString() + " " + secondName
        }
        return name
    }
}