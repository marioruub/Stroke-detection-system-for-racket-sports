package com.example.strokeDetectionSystemForRacketRports.data

import android.net.Uri
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * This class makes the requests to the Firebase Storage module.
 *
 */
class StorageService @Inject constructor() { //Firebase Storage
    /**
     * Allows to save an image with a specified name in firebase.
     *
     */
    fun saveUserImage(data: ByteArray, name: String): Boolean{
        var returnValue = true
        try {
            val imageRef = FirebaseClient.storage.reference.child("usersImages/${name}")
            imageRef.putBytes(data)
        }
        catch(e: Exception){
            returnValue = false
        }

        return returnValue
    }

    /**
     * Returns an image of the specified name from firebase.
     *
     */
    suspend fun getUserImageUrl(name: String): Uri{
        val imageRef = FirebaseClient.storage.reference.child("usersImages/${name}")
        lateinit var imageUrl: Uri
        try{
            imageUrl = imageRef.downloadUrl.await()
        }
        catch(e: Exception){
            imageUrl = Uri.EMPTY
        }

        return imageUrl
    }

    /**
     * Allows to delete an image with the specified name in firebase.
     *
     */
    fun deleteUserImage(name: String): Boolean{
        var returnValue = true
        try {
            val imageRef = FirebaseClient.storage.reference.child("usersImages/${name}")
            imageRef.delete()
        }
        catch(e: Exception){
            returnValue = false
        }

        return returnValue
    }
}