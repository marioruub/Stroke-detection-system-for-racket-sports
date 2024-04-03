package com.example.strokeDetectionSystemForRacketRports.components.statistics.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.github.mikephil.charting.data.Entry
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

/**
 * This class contains the functionality related to the statistics.
 *
 */
class StatisticsCreate @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService
) {
    /**
     * Allows to create the statistics related with all the events played.
     *
     */
    suspend fun createEventStatistic(): ArrayList<Entry>{
        var entries = arrayListOf<Entry>()

        var matchesList = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var docMatchesList = userService.getMatches().documents
        for (doc in docMatchesList){
            if (doc.exists() && isDocFromUser(doc.id) && hasEventBeenPlayed(doc)){
                val date = doc.data?.get("date").toString().split("/")
                matchesList[date[1].toInt()-1]++
            }
        }
        var i = 1
        while(i<=matchesList.size){
            entries.add(Entry(i.toFloat(), matchesList[i-1].toFloat()))
            i++
        }

        var trainingsList = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var docTrainingsList = userService.getTrainings().documents
        for (doc in docTrainingsList){
            if (doc.exists() && isDocFromUser(doc.id) && hasEventBeenPlayed(doc)){
                val date = doc.data?.get("date").toString().split("/")
                trainingsList[date[1].toInt()-1]++
            }
        }
        i = 1
        while(i<=trainingsList.size){
            entries.add(Entry(i.toFloat(), trainingsList[i-1].toFloat()))
            i++
        }

        return entries
    }

    /**
     * Allows to create the statistics related with all the strokes done.
     *
     */
    suspend fun createStrokesStatistic(): ArrayList<Entry>{
            var entries = arrayListOf<Entry>()
            var strokeList = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

            var docMatchesList = userService.getMatches().documents
            for (doc in docMatchesList){
                if (doc.exists() && isDocFromUser(doc.id) && hasEventBeenPlayed(doc)){
                    val date = doc.data?.get("date").toString().split("/")

                    val forehands = doc.data?.get("forehand").toString().toInt()
                    val backhands = doc.data?.get("backhand").toString().toInt() //Añadir más golpeos

                    strokeList[date[1].toInt()-1] += forehands + backhands
                }
            }

            var docTrainingsList = userService.getTrainings().documents
            for (doc in docTrainingsList){
                if (doc.exists() && isDocFromUser(doc.id) && hasEventBeenPlayed(doc)){
                    val date = doc.data?.get("date").toString().split("/")

                    val forehands = doc.data?.get("forehand").toString().toInt()
                    val backhands = doc.data?.get("backhand").toString().toInt() //Añadir más golpeos

                    strokeList[date[1].toInt()-1] += forehands + backhands
                }
            }

            var i = 1
            while(i<=strokeList.size){
                entries.add(Entry(i.toFloat(), strokeList[i-1].toFloat()))
                i++
            }

            return entries
    }

    /**
     * Allows to create the statistics related with all the calories burned.
     *
     */
    suspend fun createCaloriesStatistic(): ArrayList<Entry>?{
        var entries: ArrayList<Entry>?
        val doc = userService.getUserData(authenticationService.getCurrentUser()?.uid.toString())
        val gender = doc.data?.get("gender").toString()
        val weight = doc.data?.get("weight").toString()
        val height = doc.data?.get("height").toString()

        if((gender == "null" || gender == "") || (weight == "null" || weight == "") || (height == "null" || height == "")){
            entries = null
        }
        else {
            entries = arrayListOf<Entry>()
            var caloriesList = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

            var docMatchesList = userService.getMatches().documents
            for (doc in docMatchesList) {
                if (doc.exists() && isDocFromUser(doc.id) && hasEventBeenPlayed(doc)) {
                    val date = doc.data?.get("date").toString().split("/")

                    val matchTime = doc.data?.get("durationTime").toString().toFloat()
                    val weight =
                        userService.getUserData(authenticationService.getCurrentUser()?.uid.toString()).data?.get(
                            "weight"
                        ).toString().toFloat()

                    caloriesList[date[1].toInt() - 1] += calculateCalories(weight, matchTime)
                }
            }

            var docTrainingsList = userService.getTrainings().documents
            for (doc in docTrainingsList) {
                if (doc.exists() && isDocFromUser(doc.id) && hasEventBeenPlayed(doc)) {
                    val date = doc.data?.get("date").toString().split("/")

                    val trainingTime = doc.data?.get("durationTime").toString().toFloat()
                    val weight =
                        userService.getUserData(authenticationService.getCurrentUser()?.uid.toString()).data?.get(
                            "weight"
                        ).toString().toFloat()
                    caloriesList[date[1].toInt() - 1] += calculateCalories(weight, trainingTime)
                }
            }

            var i = 1
            while (i <= caloriesList.size) {
                entries.add(Entry(i.toFloat(), caloriesList[i - 1].toFloat()))
                i++
            }
        }

        return entries
    }

    /**
     * Allows to calculate the calories of the events.
     *
     */
    private fun calculateCalories(weight: Float, trainingTime: Float): Int{
        //var calories = weight * (trainingTime / 60) * 7.5f
        var calories = 0.071f * (weight * 2.2f) * (trainingTime / 60)
        return calories.toInt()
    }

    /**
     * Allows to validate whether the document is from the current user or not.
     *
     */
    private fun isDocFromUser(name: String): Boolean{
        return name.contains(authenticationService.getCurrentUser()?.uid.toString())
    }

    /**
     * Allows to validate whether the events has been already played or not.
     *
     */
    private fun hasEventBeenPlayed(doc: DocumentSnapshot): Boolean{
        return doc.data?.get("durationTime").toString() != ""
    }
}