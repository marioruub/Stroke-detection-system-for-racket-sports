package com.example.strokeDetectionSystemForRacketRports.components.authentication.view

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.strokeDetectionSystemForRacketRports.databinding.ActivityForgotPasswordBinding
import com.example.strokeDetectionSystemForRacketRports.components.authentication.viewmodel.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * This class contains the functionality related to the view of the forgotPassword activity.
 *
 */
@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    /**
     * This override method is used to start the activity and set up the user interface.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        forgotPasswordViewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        setUpTopBar()
        setUpListeners()
        setUpObservers()
    }

    /**
     * Allows to display the top bar in the forgotPassword activity.
     *
     */
    private fun setUpTopBar() = binding.topAppBar.setNavigationOnClickListener { onBackPressed() }

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners() {
        binding.emailText.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)

        binding.sendButton.setOnClickListener{
            it.dismissKeyboard()
            forgotPasswordViewModel.forgotPassword(binding.emailText.text.toString())
        }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers() {
        forgotPasswordViewModel.errorMessage.observe(this, Observer {
            if(!it){
                binding.sendText.text = "El correo introducido no existe."
                binding.sendText.setTextColor(Color.RED)
                binding.sendText.visibility = View.VISIBLE
                timeOutErrorMessage(binding.sendText, 5000)
            }
            else{
                binding.sendButton.text = "Volver a enviar"
                binding.sendText.text = "Revisa la bandeja de entrada de tu correo."
                binding.sendText.setTextColor(Color.BLACK)
                binding.sendText.visibility = View.VISIBLE
            }
        })

        forgotPasswordViewModel.emailErrorMessage.observe(this, Observer {
            binding.sendText.text = "El correo introducido no es válido."
            binding.sendText.setTextColor(Color.RED)
            binding.sendText.visibility = View.VISIBLE
            timeOutErrorMessage(binding.sendText, 5000)
        })
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