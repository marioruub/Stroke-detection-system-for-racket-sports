package com.example.strokeDetectionSystemForRacketRports.components.authentication.classes

import android.util.Patterns
import javax.inject.Inject

/**
 * This class contains the functionality related to the user data validation.
 *
 */
class UserDataValidation @Inject constructor() {

    /**
     * Allows to validate the user's email address.
     *
     */
    fun isValidEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotEmpty()
    }

    /**
     * Allows to validate the user's password.
     *
     */
    fun isValidPassword(password: String): Boolean{
        return password.length >= 6 && password.isNotEmpty()
    }

    /**
     * Allows to validate the user's both password entered.
     *
     */
    fun isValidBothPassword(password1: String, password2: String): Boolean{
        return password1.length >= 6 && password1 == password2 && password1.isNotEmpty() && password2.isNotEmpty()
    }

    /**
     * Allows to validate the user's name.
     *
     */
    fun isValidName(firstName: String, secondName: String): Boolean {
        return firstName.isNotEmpty() && secondName.isNotEmpty()
    }
}