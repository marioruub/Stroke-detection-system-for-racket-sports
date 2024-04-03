package com.example.strokeDetectionSystemForRacketRports.components.events.classes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.drawable.toDrawable
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventInfoRecyclerView
import javax.inject.Inject

/**
 * This class contains the functionality related to the all the events.
 *
 */
class EventHome @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService
){
    /**
     * Allows to get all the events data in order to create a list of events.
     *
     */
    suspend fun addEventToList(): ArrayList<EventInfoRecyclerView>{
        val queryDocuments = userService.getEvents().documents
        var uidUser = authenticationService.getCurrentUser()?.uid.toString()
        var documents = arrayListOf<EventInfoRecyclerView>()
        lateinit var tokens: List<String>

        for(document in queryDocuments){
            tokens = document.id.split("\\+".toRegex())
            if(tokens[0].trim().equals(uidUser)){
                lateinit var color: ColorDrawable
                when(document.data?.get("eventType").toString()){
                    "partido" -> color = Color.rgb(148, 217, 124).toDrawable()
                    "entrenamiento" -> color = Color.rgb(250, 211, 90).toDrawable()
                    "otroEvento" -> color = Color.rgb(114, 156, 247).toDrawable()
                }
                documents.add(EventInfoRecyclerView(document.data?.get("eventType").toString(),
                    tokens[1],
                    document.data?.get("date").toString(),
                    document.data?.get("time").toString(),
                    document.data?.get("place").toString(),
                    color))
            }
        }

        return ArrayList(orderEventList(documents))
    }

    /**
     * Allows to order a list of events based on the date and time of the event.
     *
     */
    private fun orderEventList(documents: ArrayList<EventInfoRecyclerView>): List<EventInfoRecyclerView>{
        return documents.sortedWith(compareBy<EventInfoRecyclerView>{ it.date.split("/")[2].toInt() }.thenBy { it.date.split("/")[1].toInt() }.thenBy { it.date.split("/")[0].toInt() }.thenBy{ it.time.split(":")[0].toInt() }.thenBy{ it.time.split(":")[1].toInt() })
    }
}