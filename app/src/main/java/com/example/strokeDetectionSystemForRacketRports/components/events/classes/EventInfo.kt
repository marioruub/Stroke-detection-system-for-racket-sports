package com.example.strokeDetectionSystemForRacketRports.components.events.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventInfo
import com.example.strokeDetectionSystemForRacketRports.components.events.model.MatchInfo
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * This class contains the functionality related to the information of the events.
 *
 */
class EventInfo @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService,
    private val eventDataValidation: EventDataValidation
) {
    /**
     * Allows to get all the matches and trainings information.
     *
     */
    suspend fun addMatchAndTrainingInfoToFields(eventName: String): MatchInfo{
        var matchInfo = MatchInfo("", "", "","","")
        val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventName

        val doc = userService.getEventInfo(event)
        if (doc.exists()){
            matchInfo = MatchInfo(
                doc.data?.get("date").toString(),
                doc.data?.get("time").toString(),
                doc.data?.get("place").toString(),
                doc.data?.get("courtType").toString(),
                doc.data?.get("playersNumber").toString()
            )
        }
        return matchInfo
    }

    /**
     * Allows to get all the other events information.
     *
     */
    suspend fun addOtherEventInfoToFields(eventName: String): EventInfo{
        var otherEventInfo = EventInfo("", "", "","")
        val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventName

        val doc = userService.getEventInfo(event)
        if (doc.exists()){
            otherEventInfo = EventInfo(
                doc.data?.get("date").toString(),
                doc.data?.get("time").toString(),
                doc.data?.get("place").toString(),
                doc.data?.get("description").toString()
            )
        }
        return otherEventInfo
    }

    /**
     * Allows to save all the matches and trainings information
     *
     */
    suspend fun saveMatchAndTrainingInfo(info: MatchInfo, eventName: String): Boolean{
        return if(eventDataValidation.isValidEventName(eventName) &&
            eventDataValidation.isValidEventDate(info.date) &&
            eventDataValidation.isValidEventTime(info.time) &&
            eventDataValidation.isValidEventAdress(info.place) &&
            eventDataValidation.isValidEventCoutType(info.courtType) &&
            eventDataValidation.isValidEventPlayersNumber(info.playersNumber)){
            val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventName
            userService.saveMatchAndTrainingInfo(event, info)
            true
        } else false
    }

    /**
     * Allows to save all the other events information
     *
     */
    suspend fun saveOtherEventInfo(info: EventInfo, eventName: String): Boolean{
        return if(eventDataValidation.isValidEventName(eventName) &&
            eventDataValidation.isValidEventDate(info.date) &&
            eventDataValidation.isValidEventTime(info.time) &&
            eventDataValidation.isValidEventAdress(info.place) &&
            eventDataValidation.isValidEventDescription(info.description)){
            val event = authenticationService.getCurrentUser()?.uid.toString() + "+" + eventName
            userService.saveOtherEventInfo(event, info)
            true
        } else false
    }

    /**
     * Allows to get the matches events for the next seven days.
     *
     */
    suspend fun getMatchesForNextSevenDays(): ArrayList<ArrayList<String>> {
        return getEventsByDate(userService.getMatches())
    }

    /**
     * Allows to get the trainings events for the next seven days.
     *
     */
    suspend fun getTrainingsForNextSevenDays(): ArrayList<ArrayList<String>> {
        return getEventsByDate(userService.getTrainings())
    }

    /**
     * Allows to get the other events for the next seven days.
     *
     */
    suspend fun getOtherEventsForNextSevenDays(): ArrayList<ArrayList<String>> {
        return getEventsByDate(userService.getOtherEvents())
    }

    /**
     * Allows to get all the events ardered by date.
     *
     */
    private fun getEventsByDate(docList: QuerySnapshot): ArrayList<ArrayList<String>>{
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = formatter.parse(String.format("%02d/%02d/%04d", dayOfMonth, month, year))

        val eventList: ArrayList<ArrayList<String>> = ArrayList(7)
        for (i in 0 until 7) {
            eventList.add(ArrayList())
        }
        for(doc in docList){
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = formatter.parse(doc.data?.get("date").toString())

            if(currentDate.time <= date.time && date.time <= currentDate.time + 7 * 24 * 60 * 60 * 1000 && isDocFromUser(doc.id)){
                for(i in 0 until 7){
                    if(date.time == currentDate.time + i * 24 * 60 * 60 * 1000){
                        var tokens = doc.id.split("\\+".toRegex())
                        eventList[i].add(tokens[1])
                    }
                }
            }
        }

        return eventList
    }

    /**
     * Allows to validate if the document returned from the database belongs to the current user.
     *
     */
    private fun isDocFromUser(name: String): Boolean{
        return name.contains(authenticationService.getCurrentUser()?.uid.toString())
    }
}