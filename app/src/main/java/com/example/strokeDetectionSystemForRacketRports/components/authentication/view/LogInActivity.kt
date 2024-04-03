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
import com.example.strokeDetectionSystemForRacketRports.databinding.ActivityLogInBinding
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.UserLogIn
import com.example.strokeDetectionSystemForRacketRports.components.authentication.viewmodel.AuthViewModel
import com.example.strokeDetectionSystemForRacketRports.components.homeA.view.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint

/**
 * This class contains the functionality related to the view of the logIn activity.
 *
 */
@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var authViewModel: AuthViewModel

    private val GOOGLE_SIGN_IN = 100

    /**
     * This override method is used to start the activity and set up the user interface.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyTennisApp10)
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)

        checkSession()

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        setUpListeners()
        setUpObservers()

    }

    /**
     * This override method is called when the activity is about to become visible to the user.
     *
     */
    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE
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
    private fun setUpListeners(){
        binding.emailText.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.passwordText.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)

        binding.signUpButton.setOnClickListener { showSignUpActivity() }
        binding.logInButton.setOnClickListener {
            it.dismissKeyboard()
            authViewModel.logInUser(
                UserLogIn(
                    binding.emailText.text.toString(),
                    binding.passwordText.text.toString()
                )
            )
        }

        binding.forgotPasswordText.setOnClickListener {
            it.dismissKeyboard()
            showForgotPasswarodActivity()
        }

        binding.googleButton.setOnClickListener{
            showGoogleRequestEmailActivity()
        }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers() {
        authViewModel.logInMessage.observe(this, Observer {
            if(!it){
                binding.errorMessage.text = "Introduce un correo y contraseña válidos."
                binding.errorMessage.visibility = View.VISIBLE
                timeOutErrorMessage(binding.errorMessage, 5000)
            }
            else{
                showHomeActivity(authViewModel.getCurrentUser()?.email.toString())
            }
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
     * Allows to check if there is a current logged in session.
     *
     */
    private fun checkSession(){
        val prefs = getSharedPreferences("com.example.mytennisapp10.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if(email != null){
            binding.authLayout.visibility = View.INVISIBLE
            showHomeActivity(email)
        }
        else{
            setContentView(binding.root)
        }
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
            authViewModel.logInWithGoogle(data)
        }
        else{
            Toast.makeText(this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show()
        }
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

    /**
     * Allows to show the signUp activity.
     *
     */
    private fun showSignUpActivity(){
        val signUpIntent = Intent(this, SignUpActivity::class.java)
        startActivity(signUpIntent)
    }

    /**
     * Allows to show the forgotPassword activity.
     *
     */
    private fun showForgotPasswarodActivity(){
        val forgotPasswordIntent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(forgotPasswordIntent)
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