package com.example.strokeDetectionSystemForRacketRports.components.homeF.classes

import android.graphics.Bitmap
import android.net.Uri
import com.example.strokeDetectionSystemForRacketRports.data.AuthenticationService
import com.example.strokeDetectionSystemForRacketRports.data.StorageService
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * This class contains the functionality related to the user profile image.
 *
 */
class ProfileImage @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val storageService: StorageService
) {
    /**
     * Allows to save the user profile image.
     *
     */
    fun saveUserProfileImage(bitmap: Bitmap): Boolean{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        return storageService.saveUserImage(data, authenticationService.getCurrentUser()!!.uid)
    }

    /**
     * Allows to delete the user profile image.
     *
     */
    fun deleteUserProfileImage(): Boolean{
        return storageService.deleteUserImage(authenticationService.getCurrentUser()!!.uid)
    }

    /**
     * Allows to get the user profile image.
     *
     */
    suspend fun getUserProfileImage(): Uri {
        return storageService.getUserImageUrl(authenticationService.getCurrentUser()!!.uid)
    }
}