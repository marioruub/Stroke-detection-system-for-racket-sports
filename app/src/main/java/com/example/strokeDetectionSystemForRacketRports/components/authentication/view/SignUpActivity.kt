package com.example.strokeDetectionSystemForRacketRports.components.authentication.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.databinding.ActivitySignUpBinding
import com.example.strokeDetectionSystemForRacketRports.components.authentication.viewmodel.AuthViewModel
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.UserSignUp
import com.example.strokeDetectionSystemForRacketRports.components.homeA.view.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint

/**
 * This class contains the functionality related to the view of the SignUp activity.
 *
 */
@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var authViewModel: AuthViewModel

    private val GOOGLE_SIGN_IN = 100

    /**
     * This override method is used to start the activity and set up the user interface.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        setUpTopBar()
        setUpListeners()
        setUpObservers()
    }

    /**
     * Allows to display the top bar in the signUp activity.
     *
     */
    private fun setUpTopBar() = binding.topAppBar.setNavigationOnClickListener { onBackPressed() }

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners() {
        binding.firstNameText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.secondNameText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.emailText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.passwordText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.confirmPasswordText.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)

        binding.signUpButton.setOnClickListener {
            it.dismissKeyboard()
            authViewModel.signUpUser(
                UserSignUp(
                    binding.firstNameText.text.toString(),
                    binding.secondNameText.text.toString(),
                    binding.emailText.text.toString(),
                    binding.passwordText.text.toString(),
                    binding.confirmPasswordText.text.toString()
                )
            )
        }

        binding.googleButton.setOnClickListener{
            showGoogleRequestEmailActivity()
        }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers(){
        authViewModel.signUpMessage.observe(this, Observer {
            if(!it){
                binding.errorMessage.text = "Introduce los datos del formulario correctamente."
                binding.errorMessage.visibility = View.VISIBLE
                timeOutErrorMessage(binding.errorMessage, 5000)
            }
        })

        authViewModel.goToVerifiyEmail.observe(this, Observer {
            showVerifyEmailActivity(authViewModel.getCurrentUser()?.email.toString())
        })

        authViewModel.signUpWithGoogle.observe(this, Observer {
            if(!it){
                Toast.makeText(this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show()
            }
            else{
                showHomeActivity(authViewModel.getCurrentUser()?.email.toString())
            }
        })
    }

    /**
     * Allows to show the VerifyEmail activity.
     *
     */
    private fun showVerifyEmailActivity(email: String) {
        val verifyEmailIntent = Intent(this, VerifyEmailActivity::class.java)
        verifyEmailIntent.putExtra("email", email)
        startActivity(verifyEmailIntent)
    }

    /**
     * Allows to show the Google request account dialog.
     *
     */
    private fun showGoogleRequestEmailActivity() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()

        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    /**
     * This override method is used as a callback method that receives the response from Google request account dialog.
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN && data != null){
            authViewModel.signUpWithGoogle(data)
        }
        else{
            Toast.makeText(this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Allows to show the home activity (activity where all the fragments are instantiated).
     *
     */
    private fun showHomeActivity(email: String){
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra("email", email)
        startActivity(homeIntent)
    }

    /**
     * Allows to lose the focus of a EditText when the end button is pressed.
     *
     */
    private fun EditText.loseFocusAfterAction(action: Int) {
        this.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == action) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    this.dismissKeyboard()
                v.clearFocus()
            }
            return@setOnEditorActionListener false
        }
    }

    /**
     * Allows to hide the keyboard from the screen.
     *
     */
    private fun View.dismissKeyboard(completed: () -> Unit = {}) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val wasOpened = inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        if (!wasOpened) completed()
    }

    /**
     * Allows to set up a timeout for the error message shown.
     *
     */
    fun timeOutErrorMessage(textView: TextView, time: Long){
        val timeoutHandler = Handler()
        timeoutHandler.postDelayed({ textView.visibility = View.INVISIBLE }, time)
    }
}