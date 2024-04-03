package com.example.strokeDetectionSystemForRacketRports.components.homeF.view

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.databinding.FragmentSettingsBinding
import com.example.strokeDetectionSystemForRacketRports.components.authentication.model.UserDataSettings
import com.example.strokeDetectionSystemForRacketRports.components.homeF.classes.GenderType
import com.example.strokeDetectionSystemForRacketRports.components.homeF.viewmodel.SettingsFragmentListener
import com.example.strokeDetectionSystemForRacketRports.components.homeF.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * This class contains the functionality related to the view of the settings fragment.
 *
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private var settingsListener: SettingsFragmentListener? = null
    private lateinit var settingsViewModel: SettingsViewModel
    private val GALLERY_REQUEST_CODE = 100

    /**
     * This override method is used to start the fragment.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

    /**
     * This override method is called when the fragment is attach to an activity.
     *
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is SettingsFragmentListener)
            settingsListener = context
        else
            throw IllegalArgumentException("Error")
    }

    /**
     * This override method is used to configure the layout of the UI.
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        setUpTopBar()
        setUpListeners()
        setUpObservers()

        addUserDataToFields()

        return binding.root
    }

    /**
     * Allows to display the top bar in the settings fragment.
     *
     */
    private fun setUpTopBar() = binding.topAppBar.setNavigationOnClickListener {
        removeFragment()
        showHomeFragment()
    }

    /**
     * Allows to remove a fragment.
     *
     */
    private fun removeFragment(){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.fragmentContainer) as Fragment
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
    }

    /**
     * Allows to show the home fragment.
     *
     */
    private fun showHomeFragment(){
        val bundle = Bundle()

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle
        fragmentTransaction.add(R.id.fragmentContainer, homeFragment)
        fragmentTransaction.commit()
    }

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners(){
        binding.birthDateText.setOnClickListener { showDatePickerDialog() }
        binding.genderText.setOnClickListener { showGenderSelector() }
        binding.selectUserImageButton.setOnClickListener { saveUserImage() }
        binding.deleteUserImageButton.setOnClickListener { deleteUserImage() }
        binding.modifyUserDataButton.setOnClickListener {
            it.dismissKeyboard()
            saveUserData()
        }

        binding.changePasswordButton.setOnClickListener { changePassword() }
        binding.deleteUserAccountButton.setOnClickListener { deleteUserAccount() }
        binding.logOutButton.setOnClickListener { logOutAccount() }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers(){
        lateinit var msg: String
        settingsViewModel.errorMessage1.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                msg = "Los datos de usuario se han actualizado correctamente."
                showMessageOnScreen(binding.messageText1, msg, Color.BLACK)
            }
            else{
                msg = "Introduce todos los campos correctamente."
                showMessageOnScreen(binding.messageText1, msg, Color.RED)
            }
        })

        settingsViewModel.userImage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                msg = "Foto de perfil guardada correctamente."
                showMessageOnScreen(binding.messageText1, msg, Color.BLACK)
            }
            else{
                msg = "Ha ocurrido un error al guardar la foto de perfil."
                showMessageOnScreen(binding.messageText1, msg, Color.RED)
            }
        })

        settingsViewModel.deleteUserImage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (!it){
                msg = "Ha ocurrido un error al eliminar la foto de perfil."
                showMessageOnScreen(binding.messageText1, msg, Color.RED)
            }
        })

        settingsViewModel.errorMessage2.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if(it){
                    msg = "Se ha enviado un email a tu dirección de correo, para poder cambiar la contraseña."
                    showMessageOnScreen(binding.messageText2, msg, Color.BLACK)
                }
                else{
                    msg = "Ha habido un problema enviando el email."
                    showMessageOnScreen(binding.messageText2, msg, Color.RED)
                }
        })

        settingsViewModel.userDataFields.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            if(it != null){
                binding.nameText.setText(it.firstName + " " + it.secondName, TextView.BufferType.EDITABLE)
                binding.genderText.setText(it.gender, TextView.BufferType.EDITABLE)
                binding.birthDateText.setText(it.birthDate, TextView.BufferType.EDITABLE)
                binding.weightText.setText(it.weight, TextView.BufferType.EDITABLE)
                binding.heightText.setText(it.height, TextView.BufferType.EDITABLE)
            }
            else{
                binding.nameText.setText("", TextView.BufferType.EDITABLE)
                binding.genderText.setText("", TextView.BufferType.EDITABLE)
                binding.birthDateText.setText("", TextView.BufferType.EDITABLE)
                binding.weightText.setText("", TextView.BufferType.EDITABLE)
                binding.heightText.setText("", TextView.BufferType.EDITABLE)
            }
        })
    }

    /**
     * Allows to show a date picker dialog.
     *
     */
    private fun showDatePickerDialog(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), R.style.DialogTheme ,{ _, yearSelected, monthSelected, daySelected ->
            val month = monthSelected+1
            binding.birthDateText.setText("$daySelected/$month/$yearSelected", TextView.BufferType.EDITABLE)
        }, year, month, day)
        datePickerDialog.show()
    }

    /**
     * Allows to show an alertDialog with the two types of gender.
     *
     */
    private fun showGenderSelector(){
        val items = arrayOf(GenderType.Masculino.toString(), GenderType.Femenino.toString())
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Género")
        builder.setItems(items, DialogInterface.OnClickListener { dialog, which ->
            if (which == 0) binding.genderText.setText(GenderType.Masculino.toString(), TextView.BufferType.EDITABLE)
            else binding.genderText.setText(GenderType.Femenino.toString(), TextView.BufferType.EDITABLE)
        })
        builder.show()
    }

    /**
     * Allows to save the user data.
     *
     */
    private fun saveUserData(){
        settingsViewModel.saveUserData(
            UserDataSettings(
            binding.nameText.text.toString(),
            binding.genderText.text.toString(),
            binding.birthDateText.text.toString(),
            binding.weightText.text.toString(),
            binding.heightText.text.toString()
            )
        )
    }

    /**
     * Allows to add the user data to fields.
     *
     */
    private fun addUserDataToFields(){
        settingsViewModel.addUserDataToFields()
    }

    /**
     * Allows to save the user profile image.
     *
     */
    private fun saveUserImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    /**
     * This override method is used to receive a callback from the image gallery dialog.
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val imageUri = data?.data as Uri
            val inputStream = context?.contentResolver?.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            settingsViewModel.saveUserImage(bitmap)
        }
        else{
            var msg = "Ha ocurrido un error de acceso a la galeria."
            showMessageOnScreen(binding.messageText1, msg, Color.RED)
        }
    }

    /**
     * Allows to delete the user profile image.
     *
     */
    private fun deleteUserImage(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¿Estas seguro de que deseas eliminar tu foto de perfil?")
            .setPositiveButton("Confirmar") { dialog, which ->
                settingsViewModel.deleteUserImage()
            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            .show()
    }

    /**
     * Allows to change the user password.
     *
     */
    private fun changePassword(){
        settingsViewModel.sendChangePasswordEmail()
    }

    /**
     * Allows to delete the user account.
     *
     */
    private fun deleteUserAccount(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¿Eliminar cuenta?")
            .setMessage("Se perderan todos tus datos y partidos almacenados")
            .setPositiveButton("Confirmar") { dialog, which ->
                settingsViewModel.deleteUserAccount()
                settingsViewModel.deleteUserEvents()
                settingsViewModel.deleteUserImage()
                logOutAccount()
            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            .show()
    }

    /**
     * Allows to log out the current user from the application.
     *
     */
    private fun logOutAccount(){
        settingsListener?.logOut()
    }

    /**
     * Allows to show a message on screen.
     *
     */
    private fun showMessageOnScreen(textView: TextView, text: String, color: Int){
        textView.text = text
        textView.setTextColor(color)
        textView.visibility = View.VISIBLE
        timeOutErrorMessage(textView, 5000)
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