package com.example.strokeDetectionSystemForRacketRports.components.homeF.classes

import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.DataService
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.User
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.UserDataSettings
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

/**
 * This class contains the functionality related to the user data.
 *
 */
class UserData @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: DataService,
    private val userDataSettingsValidation: UserDataSettingsValidation
) {
    /**
     * Allows to get the data from an user.
     *
     */
    suspend fun addUserDataToFields(): User {
        lateinit var firstName: String
        lateinit var secondName: String
        lateinit var gender: String
        lateinit var birthDate: String
        lateinit var weight: String
        lateinit var height: String

        lateinit var user: User

        val doc = userService.getUserData(authenticationService.getCurrentUser()?.uid.toString())
        if (doc.exists()){
            firstName = if(doc.data?.get("firstName").toString() == "null") ""
            else doc.data?.get("firstName").toString()
            secondName = if(doc.data?.get("secondName").toString() == "null") ""
            else doc.data?.get("secondName").toString()
            gender = if(doc.data?.get("gender").toString() == "null") ""
            else doc.data?.get("gender").toString()
            birthDate = if(doc.data?.get("birthDate").toString() == "null") ""
            else doc.data?.get("birthDate").toString()
            weight = if(doc.data?.get("weight").toString() == "null") ""
            else doc.data?.get("weight").toString()
            height = if(doc.data?.get("height").toString() == "null") ""
            else doc.data?.get("height").toString()

            user = User(
                authenticationService.getCurrentUser()?.uid.toString(),
                firstName,
                secondName,
                doc.data?.get("email").toString(),
                gender,
                birthDate,
                weight,
                height)
        }

        return user
    }

    /**
     * Allows to save the user data.
     *
     */
    suspend fun saveUserData(userDataSettings: UserDataSettings): Boolean{
        lateinit var firstName: String
        lateinit var secondName: String

        val fullName = userDataSettings.name.trimEnd()
        if (userDataSettingsValidation.isValidUserName(fullName) &&
            userDataSettings.gender.isNotEmpty() &&
            userDataSettings.birthDate.isNotEmpty() &&
            userDataSettingsValidation.isValidUserWeight(userDataSettings.weight) &&
            userDataSettingsValidation.isValidUserHeight(userDataSettings.height)){
            val listName = fullName.split(' ')
            if (listName.size > 3) return false
            else{
                firstName = listName.get(0)
                secondName = ""

                if(listName.size == 2){
                    secondName = listName.get(1)
                }

                if(listName.size == 3){
                    secondName = listName.get(1) + " " + listName.get(2)
                }

                userService.saveUserData(
                    authenticationService.getCurrentUser()?.uid.toString(),
                    authenticationService.getCurrentUser()?.email.toString(),
                    firstName,
                    secondName,
                    userDataSettings.gender,
                    userDataSettings.birthDate,
                    userDataSettings.weight,
                    userDataSettings.height)

                return true
            }
        }
        else return false
    }

    /**
     * Allows to get all the user info related to the home fragment.
     *
     */
    suspend fun getUserHomeInfo(): ArrayList<String>{
        var userInfo = arrayListOf<String>()
        var fullName = ""
        var secondName = ""
        val doc = userService.getUserData(authenticationService.getCurrentUser()?.uid.toString())
        if (doc.exists()){
            if(doc.data?.get("secondName").toString() != "null")
                secondName = doc.data?.get("secondName").toString()
            fullName = doc.data?.get("firstName").toString() + " " + secondName
        }
        userInfo.add(fullName)

        val docMatches = userService.getMatches().documents
        var cont = 0
        for(doc in docMatches){
            if(isDocFromUser(doc.id) && hasEventBeenPlayed(doc))
                cont++
        }
        userInfo.add(cont.toString())

        return userInfo
    }

    /**
     * Allows to validate whether the document is related to the current user or not.
     *
     */
    private fun isDocFromUser(name: String): Boolean{
        return name.contains(authenticationService.getCurrentUser()?.uid.toString())
    }

    /**
     * Allows to validate whether an event has been already played or no.
     *
     */
    private fun hasEventBeenPlayed(doc: DocumentSnapshot): Boolean{
        return doc.data?.get("durationTime").toString() != ""
    }
}