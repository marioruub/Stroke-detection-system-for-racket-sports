package com.example.strokeDetectionSystemForRacketRports.components.authentication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.strokeDetectionSystemForRacketRports.databinding.ActivityVerifyEmailBinding
import com.example.strokeDetectionSystemForRacketRports.components.authentication.viewmodel.VerifyEmailViewModel
import com.example.strokeDetectionSystemForRacketRports.components.homeA.view.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * This class contains the functionality related to the view of the VerifyEmail activity.
 *
 */
@AndroidEntryPoint
class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding
    private lateinit var verifyEmailViewModel: VerifyEmailViewModel

    private lateinit var email: String
    private var deleteUSer = false

    /**
     * This override method is used to start the activity and set up the user interface.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verifyEmailViewModel = ViewModelProvider(this).get(VerifyEmailViewModel::class.java)
        email = intent.getStringExtra("email").toString()
        setUpListeners()
        setUpObservers()

        binding.emailText.text = "Se ha enviado un correo a la dirección " + email
        binding.emailText.visibility = View.VISIBLE
    }

    /**
     * This override method is called when the activity is about to be stopped and no longer  visible for the user.
     *
     */
    override fun onStop() {
        super.onStop()
        if(!deleteUSer) {
            verifyEmailViewModel.deleteNonVerifiedAccounts()
        }
    }

    /**
     * This override method is called when the user presses the back button.
     *
     */
    override fun onBackPressed(){}

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners() {
        binding.goToHomeActivityButton.setOnClickListener{
            showHomeActivity(email)
            deleteUSer = true
        }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers() {
        verifyEmailViewModel.showButton.observe(this, Observer {
            if(it) binding.goToHomeActivityButton.visibility = View.VISIBLE
        })
    }

    /**
     * Allows to show the home activity (activity where all the fragmnets are instantiated).
     *
     */
    private fun showHomeActivity(email: String){
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra("email", email)
        startActivity(homeIntent)
    }
}