package com.example.strokeDetectionSystemForRacketRports.data

import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventInfo
import com.example.strokeDetectionSystemForRacketRports.components.events.model.MatchInfo
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Strokes
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * This class makes the requests to the Firebase Firestore module.
 *
 */
class DataService  @Inject constructor() { //Firebase Firestore

    private val USERS_COLLECTION_NAME = "users"
    private val EVENTS_COLLECTION_NAME = "events"

    /**
     * Allows to save the user data in firebase
     *
     */
    suspend fun saveUserData(uid: String, email: String, firstName: String, secondName: String, gender: String, birthDate: String, weight: String, height: String){
        if(gender.isNotEmpty() && birthDate.isNotEmpty() && weight.isNotEmpty() && height.isNotEmpty()){
            FirebaseClient.db.collection(USERS_COLLECTION_NAME).document(uid).set(
                hashMapOf(
                    "email" to email,
                    "firstName" to firstName,
                    "secondName" to secondName,
                    "gender" to gender,
                    "birthDate" to birthDate,
                    "weight" to weight,
                    "height" to height
                )
            ).await()
        }
        else{
            FirebaseClient.db.collection(USERS_COLLECTION_NAME).document(uid).set(
                hashMapOf(
                    "email" to email,
                    "firstName" to firstName,
                    "secondName" to secondName,
                    "gender" to "",
                    "birthDate" to "",
                    "weight" to "",
                    "height" to ""
                )
            ).await()
        }
    }

    /**
     * Allows to delete the user data from firebase
     *
     */
    fun deleteUserData(uid: String){
        FirebaseClient.db.collection(USERS_COLLECTION_NAME).document(uid).delete()
    }

    /**
     * Returns the user data of the user uid specified from firebase
     *
     */
    suspend fun getUserData(uid: String): DocumentSnapshot{
        return FirebaseClient.db.collection(USERS_COLLECTION_NAME).document(uid).get().await()
    }

    /**
     * Allows to delete the user events from firebase
     *
     */
    suspend fun deleteUserEvents(event: String){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).delete().await()
    }

    /**
     * Returns all the events from firebase
     *
     */
    suspend fun getEvents(): QuerySnapshot {
        return FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).get().await()
    }

    /**
     * Allows to create a match event in firebase
     *
     */
    suspend fun createMatch(event: String, placeText: String, dateText: String, timeText: String){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).set(
            hashMapOf(
                "date" to dateText,
                "time" to timeText,
                "place" to placeText,
                "courtType" to "",
                "playersNumber" to "",
                "forehand" to "0",
                "backhand" to "0",
                "serve" to "0",
                "setPoints" to mutableListOf("Oponente", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"),
                "eventType" to "partido",
                "durationTime" to ""
            )
        ).await()
    }

    /**
     * Allows to create a training event in firebase
     *
     */
    suspend fun createTraining(event: String, placeText: String, dateText: String, timeText: String){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).set(
            hashMapOf(
                "date" to dateText,
                "time" to timeText,
                "place" to placeText,
                "courtType" to "",
                "playersNumber" to "",
                "forehand" to "0",
                "backhand" to "0",
                "serve" to "0",
                "eventType" to "entrenamiento",
                "durationTime" to ""
            )
        ).await()
    }

    /**
     * Allows to create an other event in firebase
     *
     */
    suspend fun createOtherEvent(event: String, placeText: String, dateText: String, timeText: String){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).set(
            hashMapOf(
                "date" to dateText,
                "time" to timeText,
                "place" to placeText,
                "description" to "",
                "eventType" to "otroEvento"
            )
        ).await()
    }

    /**
     * Allows to save the strokes of an event in firebase
     *
     */
    suspend fun saveStrokes(event: String, strokes: Strokes, durationTime: String){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).update(
            hashMapOf(
                "forehand" to strokes.forehand,
                "backhand" to strokes.backhand,
                "serve" to strokes.serve,
                "durationTime" to durationTime
            ).toMap()
        ).await()
    }

    /**
     * Allows to ssave the reult of a match in firebase
     *
     */
    suspend fun saveMatchResult(event: String, setPoints: MutableList<String>){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).update(
            hashMapOf(
                "setPoints" to setPoints
            ).toMap()
        ).await()
    }

    /**
     * Allows to save the information of a match or a training in firebase
     *
     */
    suspend fun saveMatchAndTrainingInfo(event: String, info : MatchInfo){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).update(
            hashMapOf(
                "date" to info.date,
                "time" to info.time,
                "place" to info.place,
                "courtType" to info.courtType,
                "playersNumber" to info.playersNumber
            ).toMap()
        ).await()
    }

    /**
     * Allows to save the information of an other event in firebase
     *
     */
    suspend fun saveOtherEventInfo(event: String, info: EventInfo){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).update(
            hashMapOf(
                "date" to info.date,
                "time" to info.time,
                "place" to info.place,
                "description" to info.description,
                "eventType" to "otroEvento"
            ).toMap()
        ).await()
    }

    /**
     * Returns an event information from firebase
     *
     */
    suspend fun getEventInfo(event: String): DocumentSnapshot{
        return FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).get().await()
    }

    /**
     * Allows to delete the an event in firebase
     *
     */
    suspend fun deleteEvent(event: String){
        FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).document(event).delete().await()
    }

    /**
     * Returns the matches from firebase
     *
     */
    suspend fun getMatches(): QuerySnapshot{
        return FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).whereEqualTo("eventType", "partido").get().await()
    }

    /**
     * Returns the trainings from firebase
     *
     */
    suspend fun getTrainings(): QuerySnapshot{
        return FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).whereEqualTo("eventType", "entrenamiento").get().await()
    }

    /**
     * Returns the other events from firebase
     *
     */
    suspend fun getOtherEvents(): QuerySnapshot{
        return FirebaseClient.db.collection(EVENTS_COLLECTION_NAME).whereEqualTo("eventType", "otroEvento").get().await()
    }
}