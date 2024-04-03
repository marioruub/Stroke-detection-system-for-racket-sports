package com.example.strokeDetectionSystemForRacketRports.components.events.view

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.databinding.FragmentAllEventsBinding
import com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel.AlarmNotification
import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventInfoRecyclerView
import com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel.AlarmNotification.Companion.NOTIFICATION_ID
import com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel.AllEventsAdapter
import com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel.AllEventsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class contains the functionality related to the view of the allEvents fragment.
 *
 */
@AndroidEntryPoint
class AllEventsFragment : Fragment() {
    private lateinit var binding: FragmentAllEventsBinding
    private lateinit var allEventsViewModel: AllEventsViewModel

    private lateinit var adapter: AllEventsAdapter
    private lateinit var eventList: MutableList<EventInfoRecyclerView>
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var matchName: String
    private lateinit var trainingName: String
    private lateinit var otherEventName: String

    private lateinit var placeText: String
    private lateinit var dateText: String
    private lateinit var timeText: String

    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

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
        binding = FragmentAllEventsBinding.inflate(layoutInflater)

        setUpListeners()
        setUpObservers()
        setupRecyclerView()

        addEventsToRecyclerView()
        createChannelNotifications()

        return binding.root
    }

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners(){
        binding.matchesButton.setOnClickListener {
            showCreateEventDialog("partido")
        }

        binding.trainingButton.setOnClickListener {
            showCreateEventDialog("entrenamiento")
        }

        binding.otherEventsButton.setOnClickListener {
            showCreateEventDialog("otro evento")
        }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers(){
        allEventsViewModel.eventsNamesMatch.observe(viewLifecycleOwner, Observer {
            if(it != null){
                if(!it.contains(matchName)){
                    //Crear notificación
                    scheduleNotification(dateText, timeText, matchName)

                    val bundle = Bundle()
                    bundle.putString("matchName", matchName)
                    bundle.putString("placeText", placeText)
                    bundle.putString("dateText", dateText)
                    bundle.putString("timeText", timeText)

                    val matchFragment = MatchFragment()
                    matchFragment.arguments = bundle
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, matchFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                else
                    showMessageOnScreen(binding.errorMessage, "El nombre del evento ya existe.", Color.RED)
            }
            else
                showMessageOnScreen(binding.errorMessage, "Ha habido un problema de conexión", Color.RED)
        })

        allEventsViewModel.eventsNamesTraining.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                if(!it.contains(trainingName)){
                    //Crear notificación
                    scheduleNotification(dateText, timeText, trainingName)

                    val bundle = Bundle()
                    bundle.putString("trainingName", trainingName)
                    bundle.putString("placeText", placeText)
                    bundle.putString("dateText", dateText)
                    bundle.putString("timeText", timeText)

                    val trainingFragment = TrainingFragment()
                    trainingFragment.arguments = bundle
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, trainingFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                else
                    showMessageOnScreen(binding.errorMessage, "El nombre del evento ya existe.", Color.RED)
            }
            else
                showMessageOnScreen(binding.errorMessage, "Ha habido un problema de conexión", Color.RED)
        })

        allEventsViewModel.eventsNamesOtherEvent.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                if(!it.contains(otherEventName)){
                    //Crear notificación
                    scheduleNotification(dateText, timeText, otherEventName)

                    val bundle = Bundle()
                    bundle.putString("eventName", otherEventName)
                    bundle.putString("placeText", placeText)
                    bundle.putString("dateText", dateText)
                    bundle.putString("timeText", timeText)

                    val otherEventFragment = OtherEventFragment()
                    otherEventFragment.arguments = bundle
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, otherEventFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                else
                    showMessageOnScreen(binding.errorMessage, "El nombre del evento ya existe.", Color.RED)
            }
            else
                showMessageOnScreen(binding.errorMessage, "Ha habido un problema de conexión", Color.RED)
        })

        allEventsViewModel.infoRecyclerView.observe(viewLifecycleOwner, Observer {
            if(it != null){
                for(i in it){
                    eventList.add(i)
                    adapter.notifyItemInserted(0)
                    binding.eventsRecyclerView.scrollToPosition(0)
                }
            }
            else
                showMessageOnScreen(binding.errorMessage, "Ha habido un problema de conexión", Color.RED)
        })
    }

    /**
     * Allows to set up the recyclerView used to show all the events.
     *
     */
    private fun setupRecyclerView() {
        eventList = mutableListOf()
        adapter = AllEventsAdapter(eventList) {
            when(it.eventType){
                "partido" -> showMatchFragment(it)
                "entrenamiento" -> showTrainingFragment(it)
                "otroEvento" -> showOtherEventFragment(it)
            }
        }
        binding.eventsRecyclerView.adapter = adapter

        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.eventsRecyclerView.layoutManager = layoutManager
        binding.eventsRecyclerView.isNestedScrollingEnabled = false

        val animator = binding.eventsRecyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    /**
     * Allows to show the match event fragment.
     *
     */
    private fun showMatchFragment(event: EventInfoRecyclerView){
        val bundle = Bundle()
        bundle.putString("matchName", event.name)
        bundle.putString("placeText", event.place)
        bundle.putString("dateText", event.date)
        bundle.putString("timeText", event.time)

        val matchFragment = MatchFragment()
        matchFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, matchFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Allows to show the training event fragment.
     *
     */
    private fun showTrainingFragment(event: EventInfoRecyclerView){
        val bundle = Bundle()
        bundle.putString("trainingName", event.name)
        bundle.putString("placeText", event.place)
        bundle.putString("dateText", event.date)
        bundle.putString("timeText", event.time)

        val trainingFragment = TrainingFragment()
        trainingFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, trainingFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Allows to show the other event fragment.
     *
     */
    private fun showOtherEventFragment(event: EventInfoRecyclerView){
        val bundle = Bundle()
        bundle.putString("eventName", event.name)
        bundle.putString("placeText", event.place)
        bundle.putString("dateText", event.date)
        bundle.putString("timeText", event.time)

        val otherEventFragment = OtherEventFragment()
        otherEventFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, otherEventFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Allows to add the all the events to the recyclerView.
     *
     */
    private fun addEventsToRecyclerView(){
        allEventsViewModel.addEventsToRecyclerView()
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
     * Allows to show a date picker dialog.
     *
     */
    private fun showDatePickerDialog(textView: TextView){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), R.style.DialogTheme ,{ _, yearSelected, monthSelected, daySelected ->
            val month = monthSelected+1
            textView.setText("$daySelected/$month/$yearSelected", TextView.BufferType.EDITABLE)
        }, year, month, day)
        datePickerDialog.show()
    }

    /**
     * Allows to show a time picker dialog.
     *
     */
    private fun showTimePickerDialog(textView: TextView){
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

                textView.setText(timeString, TextView.BufferType.EDITABLE)
            }, hour, minute, true)

        timePickerDialog.show()
    }

    /**
     * Allows to show an AlertDialog used to create an event.
     *
     */
    private fun showCreateEventDialog(title: String){
        val builder = AlertDialog.Builder(context, R.style.DialogTheme)
        builder.setTitle("Nombre del " + title)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_create_match, null)
        builder.setView(view)

        val eventName = view.findViewById<EditText>(R.id.eventNameText1)
        val place = view.findViewById<EditText>(R.id.placeText1)
        val date = view.findViewById<EditText>(R.id.dateText1)
        date.setOnClickListener { showDatePickerDialog(date) }
        val time = view.findViewById<EditText>(R.id.timeText1)
        time.setOnClickListener { showTimePickerDialog(time) }

        builder.setPositiveButton("Aceptar") { _, _ ->
            if(eventName.text.toString().isNotEmpty() && place.text.toString().isNotEmpty() && date.text.toString().isNotEmpty() && time.text.toString().isNotEmpty()){
                when(title){
                    "partido" -> matchName = eventName.text.toString()
                    "entrenamiento" -> trainingName = eventName.text.toString()
                    "otro evento" -> otherEventName = eventName.text.toString()
                }
                placeText = place.text.toString()
                dateText = date.text.toString()
                timeText = time.text.toString()

                when(title){
                    "partido" -> allEventsViewModel.getEventsNamesMatch()
                    "entrenamiento" -> allEventsViewModel.getEventsNamesTraining()
                    "otro evento" -> allEventsViewModel.getEventsNamesOtherEvent()
                }
            }
            else
                showMessageOnScreen(binding.errorMessage, "Introduce todos los campos.", Color.RED)
        }

        builder.setNegativeButton("Cancelar") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Allows to create a new notification.
     *
     */
    private fun scheduleNotification(dateText: String, timeText: String, event: String) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = dateFormat.parse(dateText)!!
        val time = timeFormat.parse(timeText)!!

        val alarmTime = Calendar.getInstance().apply {
            setTime(date)
            set(Calendar.HOUR_OF_DAY, time.hours)
            set(Calendar.MINUTE, time.minutes)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        if(alarmTime > Calendar.getInstance().timeInMillis){
            val intent = Intent(context, AlarmNotification::class.java)
            intent.putExtra("eventName", event)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        }
    }

    /**
     * Allows to create a channel for the notifications.
     *
     */
    private fun createChannelNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "SUSCRIBETE"
            }

            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
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