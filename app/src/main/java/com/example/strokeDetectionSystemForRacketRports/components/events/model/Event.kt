package com.example.strokeDetectionSystemForRacketRports.components.events.model

import android.graphics.drawable.ColorDrawable

/**
 * This data class contains all the attributes of the information of a match event.
 *
 */
data class MatchInfo(var date: String,
                     var time: String,
                     var place: String,
                     var courtType: String,
                     var playersNumber: String)

/**
 * This data class contains all the attributes of the information of a training event.
 *
 */
data class TrainingInfo(var date: String,
                     var time: String,
                     var place: String,
                     var courtType: String,
                     var playersNumber: String)

/**
 * This data class contains all the attributes of the information of a other event.
 *
 */
data class EventInfo(var date: String,
                     var time: String,
                     var place: String,
                     var description: String)

/**
 * This data class contains all the attributes of the results of a match event.
 *
 */
data class MatchResult(var playerName: String,
                       var firstSet1: String,
                       var firstSet2: String,
                       var secondSet1: String,
                       var secondSet2: String,
                       var thirdSet1: String,
                       var thirdSet2: String,
                       var fourthSet1: String,
                       var fourthSet2: String,
                       var fifthSet1: String,
                       var fifthSet2: String)

/**
 * This data class contains all the attributes needed for the list of events.
 *
 */
data class EventInfoRecyclerView(var eventType: String,
                                 var name: String,
                                 var date: String,
                                 var time: String,
                                 var place: String,
                                 var color: ColorDrawable)

/**
 * This data class contains all the attributes needed to create an event.
 *
 */
data class EventCreateInfo(var name: String,
                           var date: String,
                           var time: String,
                           var place: String)
