package com.example.strokeDetectionSystemForRacketRports.components.events.view

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProvider
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.databinding.FragmentOtherEventBinding
import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventCreateInfo
import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventInfo
import com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel.AlarmNotification
import com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel.AllEventsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class contains the functionality related to the view of the other event fragment.
 *
 */
@AndroidEntryPoint
class OtherEventFragment : Fragment() {
    private lateinit var binding: FragmentOtherEventBinding
    private lateinit var allEventsViewModel: AllEventsViewModel

    private lateinit var eventName: String
    private lateinit var placeText: String
    private lateinit var dateText: String
    private lateinit var timeText: String

    /**
     * This override method is used to start the fragment.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allEventsViewModel = ViewModelProvider(this).get(AllEventsViewModel::class.java)
    }

    /**
     * This override method is used to configure the layout of the UI.
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOtherEventBinding.inflate(layoutInflater)
        eventName = arguments?.getString("eventName").toString()
        placeText = arguments?.getString("placeText").toString()
        dateText = arguments?.getString("dateText").toString()
        timeText = arguments?.getString("timeText").toString()

        setUpTopAppBar()
        setUpListeners()
        setUpObservers()

        createEvent()
        addEventInfoToFields()

        return binding.root
    }

    /**
     * Allows to display the top bar in the otherEvent fragment.
     *
     */
    private fun setUpTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener { showEventsFragment() }
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.menuTopAppBar -> {
                    deleteOtherEvent()
                    true
                }
                else -> {true}
            }
        }
    }

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners(){
        binding.dateText.setOnClickListener { showDatePickerDialog() }
        binding.timeText.setOnClickListener { showTimePickerDialog() }
        binding.addInfoButton.setOnClickListener { saveEventInfo() }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers(){
        allEventsViewModel.errorMessage2.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            if(it)
                showMessageOnScreen(binding.errorMessage2, "Información guardada correctamente.", Color.BLACK)
            else
                showMessageOnScreen(binding.errorMessage2, "Introduce los campos correctamente.", Color.RED)
        })

        allEventsViewModel.otherEventInfo.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            if(it != null){
                binding.dateText.setText(it.date)
                binding.timeText.setText(it.time)
                binding.placeText.setText(it.place)
                binding.otherEventDescriptionText.setText(it.description)
            }
        })

        allEventsViewModel.deleteMatch.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            showEventsFragment()
        })
    }

    /**
     * Allows to set all the information needed to create the other event.
     *
     */
    private fun createEvent(){
        allEventsViewModel.createOtherEvent(EventCreateInfo(eventName, dateText, timeText, placeText))
    }

    /**
     * Allows to add all the other event information to the fields.
     *
     */
    private fun addEventInfoToFields(){
        binding.otherEventNameText.text = eventName
        binding.placeText.setText(placeText)
        binding.dateText.setText(dateText)
        binding.timeText.setText(timeText)

        allEventsViewModel.addOtherEventInfoToFields(eventName)
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
            binding.dateText.setText("$daySelected/$month/$yearSelected", TextView.BufferType.EDITABLE)
        }, year, month, day)
        datePickerDialog.show()
    }

    /**
     * Allows to show a time picker dialog.
     *
     */
    private fun showTimePickerDialog(){
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, R.style.DialogTheme,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val timeString = timeFormat.format(calendar.time)

                binding.timeText.setText(timeString, TextView.BufferType.EDITABLE)
            }, hour, minute, true)

        timePickerDialog.show()
    }

    /**
     * Allows to save the information of an other event.
     *
     */
    private fun saveEventInfo(){
        allEventsViewModel.saveOtherEventInfo(EventInfo(dateText, timeText, placeText, binding.otherEventDescriptionText.text.toString()), eventName)
    }

    /**
     * Allows to show a message.
     *
     */
    private fun showMessageOnScreen(textView: TextView, text: String, color: Int){
        textView.text = text
        textView.setTextColor(color)
        textView.visibility = View.VISIBLE
        timeOutErrorMessage(textView, 5000)
    }

    /**
     * Allows to show allEvents fragment.
     *
     */
    private fun showEventsFragment(){
        val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainer) as Fragment
        val fragmentTransaction1 = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction1.remove(fragment).commit()

        val fragmentTransaction2 = requireActivity().supportFragmentManager.beginTransaction()
        val matchesFragment = AllEventsFragment()
        fragmentTransaction2.add(R.id.fragmentContainer, matchesFragment)
        fragmentTransaction2.commit()
    }

    /**
     * Allows to delete an other event.
     *
     */
    private fun deleteOtherEvent(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¿Eliminar Evento?")
            .setMessage("Se perderan todos los datos almacenados")
            .setPositiveButton("Confirmar") { dialog, which ->
                allEventsViewModel.deleteEvent(eventName)
                deleteNotification()
            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            .show()
    }

    /**
     * Allows to delete the notification related with an other event.
     *
     */
    private fun deleteNotification(){
        val intent = Intent(context, AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmNotification.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
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