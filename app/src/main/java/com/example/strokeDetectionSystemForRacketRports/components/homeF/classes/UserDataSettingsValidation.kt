package com.example.strokeDetectionSystemForRacketRports.components.homeF.classes

import android.util.Patterns
import javax.inject.Inject

/**
 * This class contains the functionality related to the validation of the user data.
 *
 */
class UserDataSettingsValidation @Inject constructor() {
    /**
     * Allows to validate the user name.
     *
     */
    fun isValidUserName(name: String): Boolean{
        return name.isNotEmpty()
    }

    /**
     * Allows to validate the user email
     *
     */
    fun isValidUserEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotEmpty()
    }

    /**
     * Allows to validate the user weight.
     *
     */
    fun isValidUserWeight(weight: String): Boolean{
        return weight.isNotEmpty() && weight.toInt() >= 0 && weight.toInt() < 200
    }

    /**
     * Allows to validate the user height.
     *
     */
    fun isValidUserHeight(height: String): Boolean{
        return height.isNotEmpty() && height.toInt() >= 0 && height.toInt() < 220
    }
}