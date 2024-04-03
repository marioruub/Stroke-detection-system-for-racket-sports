package com.example.strokeDetectionSystemForRacketRports.components.authentication.model

/**
 * This data class contains all the attributes of the user.
 *
 */
data class User (var uid: String,
                 var firstName: String,
                 var secondName: String,
                 var email: String,
                 var gender: String,
                 var birthDate: String,
                 var weight: String,
                 var height: String)

/**
 * This data class contains the attributes of the user while signing up.
 *
 */
data class UserSignUp (var firstName: String,
                        var secondName: String,
                        var email: String,
                        var password: String,
                        var confirmPassword: String)

/**
 * This data class contains the attributes of the user while loging in.
 *
 */
data class UserLogIn (var email: String,
                        var password: String)

/**
 * This data class contains only the attributes of the user data.
 *
 */
data class UserDataSettings(var name: String,
                            var gender: String,
                            var birthDate: String,
                            var weight: String,
                            var height: String )