package com.example.strokeDetectionSystemForRacketRports.components.events.view

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProvider
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.databinding.FragmentMatchBinding
import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventCreateInfo
import com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel.AlarmNotification
import com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel.AllEventsViewModel
import com.example.strokeDetectionSystemForRacketRports.components.events.model.MatchInfo
import java.text.SimpleDateFormat
import com.example.strokeDetectionSystemForRacketRports.components.events.model.MatchResult
import com.example.strokeDetectionSystemForRacketRports.components.strokes.classes.StrokeType
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Strokes
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.util.*

/**
 * This class contains the functionality related to the view of the match fragment.
 *
 */
@AndroidEntryPoint
class MatchFragment : Fragment() {
    private lateinit var binding: FragmentMatchBinding
    private lateinit var allEventsViewModel: AllEventsViewModel

    private lateinit var matchName: String
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

        binding = FragmentMatchBinding.inflate(layoutInflater)
        matchName = arguments?.getString("matchName").toString()
        placeText = arguments?.getString("placeText").toString()
        dateText = arguments?.getString("dateText").toString()
        timeText = arguments?.getString("timeText").toString()

        setUpTopAppBar()
        setUpListeners()
        setUpObservers()

        createMatch()
        addMatchInfoToFields()
        addMatchResultsToFields()
        getStrokes()

        return binding.root
    }

    /**
     * Allows to display the top bar in the match fragment.
     *
     */
    private fun setUpTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener { showEventsFragment() }
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.menuTopAppBar -> {
                    deleteMatch()
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
        binding.matchInfoButton.setOnClickListener { showOrHideInfoDialog() }
        binding.dateText.setOnClickListener { showDatePickerDialog() }
        binding.timeText.setOnClickListener { showTimePickerDialog() }
        binding.courtTypeText.setOnClickListener { showCourtTypeDialog() }
        binding.playersNumberText.setOnClickListener { showPlayersNumberDialog() }
        binding.addInfoButton.setOnClickListener { saveMatchInfo() }
        binding.addMatchResultButton.setOnClickListener { showResultDialog() }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers(){
        allEventsViewModel.matchResults.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            if(it != null){
                try{
                    binding.oponentNameText.text = it?.get(0)
                    binding.firstSet1Text.text = it?.get(1)
                    binding.firstSet2Text.text = it?.get(2)
                    binding.secondSet1Text.text = it?.get(3)
                    binding.secondSet2Text.text = it?.get(4)
                    binding.thirdSet1Text.text = it?.get(5)
                    binding.thirdSet2Text.text = it?.get(6)
                    binding.fourthSet1Text.text = it?.get(7)
                    binding.fourthSet2Text.text = it?.get(8)
                    binding.fifthSet1Text.text = it?.get(9)
                    binding.fifthSet2Text.text = it?.get(10)

                    binding.firstSet1Text.typeface = Typeface.DEFAULT
                    binding.firstSet2Text.typeface = Typeface.DEFAULT
                    binding.secondSet1Text.typeface = Typeface.DEFAULT
                    binding.secondSet2Text.typeface = Typeface.DEFAULT
                    binding.thirdSet1Text.typeface = Typeface.DEFAULT
                    binding.thirdSet2Text.typeface = Typeface.DEFAULT
                    binding.fourthSet1Text.typeface = Typeface.DEFAULT
                    binding.fourthSet2Text.typeface = Typeface.DEFAULT
                    binding.fifthSet1Text.typeface = Typeface.DEFAULT
                    binding.fifthSet2Text.typeface = Typeface.DEFAULT

                    textBold(it, 1, 2, binding.firstSet1Text, binding.firstSet2Text)
                    textBold(it, 3, 4, binding.secondSet1Text, binding.secondSet2Text)
                    textBold(it, 5, 6, binding.thirdSet1Text, binding.thirdSet2Text)
                    textBold(it, 7, 8, binding.fourthSet1Text, binding.fourthSet2Text)
                    textBold(it, 9, 10, binding.fifthSet1Text, binding.fifthSet2Text)
                }
                catch(e: Exception){}
            }
        })

        allEventsViewModel.errorMessage2.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            if(it)
                showMessageOnScreen(binding.errorMessage2, "Resultados guardados correctamente.", Color.BLACK)
            else
                showMessageOnScreen(binding.errorMessage2, "Introduce los campos correctamente.", Color.RED)
        })

        allEventsViewModel.playerName.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            if(it.isEmpty())
                binding.playerNameText.text = "Oponente"
            else
                binding.playerNameText.text = it
        })

        allEventsViewModel.errorMessage1.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            if(it)
                showMessageOnScreen(binding.errorMessage1, "Información guardada correctamente.", Color.BLACK)
            else
                showMessageOnScreen(binding.errorMessage1, "Introduce los campos correctamente.", Color.RED)
        })

        allEventsViewModel.matchInfo.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            if(it != null){
                binding.dateText.setText(it.date)
                binding.timeText.setText(it.time)
                binding.placeText.setText(it.place)
                binding.courtTypeText.setText(it.courtType)
                binding.playersNumberText.setText(it.playersNumber)
            }
        })

        allEventsViewModel.strokes.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            showPieChart(it)
        })

        allEventsViewModel.deleteMatch.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            showEventsFragment()
        })
    }

    /**
     * Allows to set the bold property to a text depending on two indexes.
     *
     */
    private fun textBold(it: MutableList<String>?, index1: Int, index2: Int, textView1: TextView, textView2: TextView){
        if(it?.get(index1)?.toInt()!! > it.get(index2).toInt())
            textView1.typeface = Typeface.DEFAULT_BOLD
        else if (it?.get(index1)?.toInt()!! < it.get(index2).toInt())
            textView2.typeface = Typeface.DEFAULT_BOLD
    }

    /**
     * Allows to set all the information needed to create a match event.
     *
     */
    private fun createMatch(){
        allEventsViewModel.createMatch(EventCreateInfo(matchName, dateText, timeText, placeText))
    }

    /**
     * Allows to add all the match information to the fields.
     *
     */
    private fun addMatchInfoToFields(){
        binding.matchNameText.text = matchName
        binding.placeText.setText(placeText)
        binding.dateText.setText(dateText)
        binding.timeText.setText(timeText)

        allEventsViewModel.addMatchAndTrainingInfoToFields(matchName)
    }

    /**
     * Allows to add all the match results to the fields.
     *
     */
    private fun addMatchResultsToFields(){
        allEventsViewModel.addMatchResultsToFields(matchName)
        allEventsViewModel.addPlayerNameToField()
    }

    /**
     * Allows to show or hide the information dialog.
     *
     */
    private fun showOrHideInfoDialog(){
        if (binding.firstContainer.visibility == View.GONE) {
            val anim = AnimationUtils.loadAnimation(context, R.anim.slide_down)
            anim.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.thirdContainer.visibility = View.INVISIBLE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })

            val anim2 = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            binding.thirdContainer.startAnimation(anim2)

            binding.firstContainer.startAnimation(anim)
            binding.firstContainer.visibility = View.VISIBLE

            val drawable = resources.getDrawable(R.drawable.baseline_keyboard_arrow_down_24)
            binding.matchInfoButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
        } else{
            val anim = AnimationUtils.loadAnimation(context, R.anim.slide_up)
            anim.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.firstContainer.visibility = View.GONE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })

            val anim2 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            binding.thirdContainer.startAnimation(anim2)

            binding.firstContainer.startAnimation(anim)
            binding.thirdContainer.visibility = View.VISIBLE

            val drawable = resources.getDrawable(R.drawable.baseline_keyboard_arrow_up_24)
            binding.matchInfoButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
        }
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
     * Allows to show an alertDialog with three types of courts.
     *
     */
    private fun showCourtTypeDialog(){
        val items = arrayOf("Tierra batida", "Césped", "Pista dura")
        val builder = AlertDialog.Builder(context, R.style.DialogTheme)
        builder.setTitle("Tipo de pista")
        builder.setItems(items, DialogInterface.OnClickListener { dialog, which ->
            when(which){
                0 -> binding.courtTypeText.setText("Tierra batida", TextView.BufferType.EDITABLE)
                1 -> binding.courtTypeText.setText("Césped", TextView.BufferType.EDITABLE)
                2 -> binding.courtTypeText.setText("Pista dura", TextView.BufferType.EDITABLE)
            }
        })
        builder.show()
    }

    /**
     * Allows to show an alertDialog with two the types of players numbers in a match event.
     *
     */
    private fun showPlayersNumberDialog(){
        val items = arrayOf("Individual", "Dobles")
        val builder = AlertDialog.Builder(context, R.style.DialogTheme)
        builder.setTitle("Número de jugadores")
        builder.setItems(items, DialogInterface.OnClickListener { dialog, which ->
            when(which){
                0 -> binding.playersNumberText.setText("Individual", TextView.BufferType.EDITABLE)
                1 -> binding.playersNumberText.setText("Dobles", TextView.BufferType.EDITABLE)
            }
        })
        builder.show()
    }

    /**
     * Allows to save the information of a match event.
     *
     */
    private fun saveMatchInfo(){
        allEventsViewModel.saveMatchAndTrainingInfo(MatchInfo(binding.dateText.text.toString(),
            binding.timeText.text.toString(),
            binding.placeText.text.toString(),
            binding.courtTypeText.text.toString(),
            binding.playersNumberText.text.toString()),
            matchName
        )
    }

    /**
     * Allows to show an alertDialog to add all the match results.
     *
     */
    private fun showResultDialog(){
        val builder = AlertDialog.Builder(context, R.style.DialogTheme)
        builder.setTitle("Resultado Partido")
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_match_result, null)
        builder.setView(view)

        builder.setPositiveButton("Aceptar") { _, _ ->

            val playerName = view.findViewById<EditText>(R.id.playerNameText)
            val firstSet1 = view.findViewById<EditText>(R.id.firstSet1Text)
            val firstSet2 = view.findViewById<EditText>(R.id.firstSet2Text)
            val secondSet1 = view.findViewById<EditText>(R.id.secondSet1Text)
            val secondSet2 = view.findViewById<EditText>(R.id.secondSet2Text)
            val thirdSet1 = view.findViewById<EditText>(R.id.thirdSet1Text)
            val thirdSet2 = view.findViewById<EditText>(R.id.thirdSet2Text)
            val fourthSet1 = view.findViewById<EditText>(R.id.fourthSet1Text)
            val fourthSet2 = view.findViewById<EditText>(R.id.fourthSet2Text)
            val fifthSet1 = view.findViewById<EditText>(R.id.fifthSet1Text)
            val fifthSet2 = view.findViewById<EditText>(R.id.fifthSet2Text)

            allEventsViewModel.saveMatchResult(MatchResult(
                playerName.text.toString(),
                firstSet1.text.toString(),
                firstSet2.text.toString(),
                secondSet1.text.toString(),
                secondSet2.text.toString(),
                thirdSet1.text.toString(),
                thirdSet2.text.toString(),
                fourthSet1.text.toString(),
                fourthSet2.text.toString(),
                fifthSet1.text.toString(),
                fifthSet2.text.toString()
            ), matchName)
        }

        builder.setNegativeButton("Cancelar") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Allows to get all strokes of match event.
     *
     */
    private fun getStrokes(){
        allEventsViewModel.addStrokesToPieChart(matchName)
    }

    /**
     * Allows to show the pie chart with all the strokes.
     *
     */
    private fun showPieChart(strokes: Strokes){
        if(strokes.forehand != 0 || strokes.backhand != 0){ //Añadir entradas
            binding.pieChart.visibility = View.VISIBLE
            binding.notStartedMatchText.visibility = View.INVISIBLE

            val entries = mutableListOf(
                PieEntry(strokes.backhand.toFloat(), StrokeType.REVÉS.toString()),
                PieEntry(strokes.forehand.toFloat(), StrokeType.DERECHA.toString()),
                //PieEntry(strokes.serve.toFloat(), StrokeType.SAQUE.toString())
            )

            var i = 0
            while(i<entries.size){
                if(entries.get(i).value == 0f) entries.removeAt(i)
                i++
            }

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = ColorTemplate.COLORFUL_COLORS.toMutableList()
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 18f

            val pieData = PieData(dataSet)
            val decimalFormat = DecimalFormat("###")
            pieData.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return decimalFormat.format(value.toDouble())
                }
            })

            binding.pieChart.data = pieData
            binding.pieChart.legend.textSize = 20f
            binding.pieChart.centerText = ""
            binding.pieChart.setCenterTextSize(18f)
            binding.pieChart.description.isEnabled = false
            binding.pieChart.setDrawEntryLabels(true)
            binding.pieChart.setTouchEnabled(true)
            binding.pieChart.invalidate()
            binding.pieChart.legend.textSize = 15f
            binding.pieChart.legend.xEntrySpace = 30f
        }
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
     * Allows to delete a match event.
     *
     */
    private fun deleteMatch(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¿Eliminar Evento?")
            .setMessage("Se perderan todos los datos almacenados")
            .setPositiveButton("Confirmar") { dialog, which ->
                allEventsViewModel.deleteEvent(matchName)
                deleteNotification()
            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            .show()
    }

    /**
     * Allows to delete the notification related with a match event.
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