package com.example.strokeDetectionSystemForRacketRports.components.strokes.view

import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.strokeDetectionSystemForRacketRports.databinding.ActivityStrokesBinding
import com.example.strokeDetectionSystemForRacketRports.components.strokes.classes.StrokeType
import com.example.strokeDetectionSystemForRacketRports.components.strokes.viewmodel.StrokesAdapter
import com.example.strokeDetectionSystemForRacketRports.components.strokes.viewmodel.StrokesViewModel
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Stroke
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Strokes
import com.example.strokeDetectionSystemForRacketRports.util.ble.ConnectionEventListener
import com.example.strokeDetectionSystemForRacketRports.util.ble.ConnectionManager
import com.example.strokeDetectionSystemForRacketRports.util.ble.toHexString
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.util.UUID

/**
 * This class contains the functionality related to the view of the strokes activity.
 *
 */
@AndroidEntryPoint
class StrokesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStrokesBinding
    private lateinit var strokesViewModel: StrokesViewModel

    private var strokes = Strokes(0, 0, 0)
    //private var strokes = Strokes(0, 0)
    private lateinit var strokeList: MutableList<Stroke>
    private lateinit var adapter: StrokesAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var newStroke: Stroke
    private var areStrokesSaved = false
    private var iniTime: Long = 0
    private var endTime: Long = 0

    //Bluetooth Low Energy
    private lateinit var device: BluetoothDevice
    private val characteristics by lazy {
        ConnectionManager.servicesOnDevice(device)?.flatMap { service ->
            service.characteristics ?: listOf()
        } ?: listOf()
    }
    private var notifyingCharacteristics = mutableListOf<UUID>()

    /**
     * This override method is used to start the fragment.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        ConnectionManager.registerListener(connectionEventListener)
        super.onCreate(savedInstanceState)
        binding = ActivityStrokesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            ?: error("Falta dispositivo BLE.")

        strokesViewModel = ViewModelProvider(this).get(StrokesViewModel::class.java)

        setUpTopBar()
        setUpListeners()
        setUpObservers()

        showPieChart()
        setupRecyclerView()

        iniTime = System.currentTimeMillis()
    }

    /**
     * This override method is called when the fragment is visible to the user.
     *
     */
    override fun onResume() {
        super.onResume()

        for (characteristic in characteristics) {
            when (characteristic.uuid) {
                UUID.fromString("42421101-5A22-46DD-90F7-7AF26F723159") -> { //Característica del modelo del arduino
                    if (notifyingCharacteristics.contains(characteristic.uuid)) {
                        ConnectionManager.disableNotifications(device, characteristic)
                    } else {
                        ConnectionManager.enableNotifications(device, characteristic)
                    }
                }
            }
        }
    }

    /**
     * This override method is called when the fragment is being destroyed.
     *
     */
    override fun onDestroy() {
        disconnectBleConnection()
        super.onDestroy()
    }

    /**
     * This override method is used to go to the last activity or fragment.
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * This variable is used to listen to the characteristic of the model.
     *
     */
    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onDisconnect = {}

            onCharacteristicChanged = { _, characteristic ->
                Handler(Looper.getMainLooper()).post {
                    when (characteristic.value.toHexString()) {
                        "0x00 00 01 00" -> {
                            newStroke = Stroke(StrokeType.DERECHA.toString())
                            strokes.forehand = strokes.forehand + 1
                        }
                        "0x00 00 02 00" -> {
                            newStroke = Stroke(StrokeType.REVÉS.toString())
                            strokes.backhand = strokes.backhand + 1
                        }
                        "0x00 00 03 00" -> {
                            newStroke = Stroke(StrokeType.SAQUE.toString())
                            strokes.serve = strokes.serve + 1
                        }
                    }
                    showPieChart()

                    adapter.addStroke(newStroke)
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        }
    }

    /**
     * Allows to disconnect from the current Ble connection.
     *
     */
    private fun disconnectBleConnection(){
        ConnectionManager.unregisterListener(connectionEventListener)
        ConnectionManager.teardownConnection(device)
    }

    /**
     * Allows to display the top bar in the strokes activity.
     *
     */
    private fun setUpTopBar() = binding.topAppBar.setNavigationOnClickListener { onBackPressed() }

    /**
     * This override method is called when the user press the back button.
     *
     */
    override fun onBackPressed() {
        if(!areStrokesSaved && (strokes.forehand != 0 || strokes.backhand != 0 || strokes.serve != 0)) {
        //if(!areStrokesSaved && (strokes.forehand != 0 || strokes.backhand != 0)){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Deseas volver para atrás?")
                .setMessage("Se perderan todos los golpeos realizados en esta sesión")
                .setPositiveButton("Sí") { dialog, which ->
                    super.onBackPressed()
                }
                .setNegativeButton("No") { dialog, which ->

                }
                .show()
        }
        else
            super.onBackPressed()
    }

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners(){
        binding.topTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab == binding.topTab.getTabAt(0))
                    binding.pieChart.visibility = View.VISIBLE
                else
                    binding.recyclerView.visibility = View.VISIBLE
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab == binding.topTab.getTabAt(0))
                    binding.pieChart.visibility = View.INVISIBLE
                else
                    binding.recyclerView.visibility = View.INVISIBLE
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.endSessionButton.setOnClickListener { endStrokeSession() }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers(){
        strokesViewModel.eventsNames.observe(this, Observer {
            if(it != null){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Selector de eventos")
                builder.setItems(it.toTypedArray(), DialogInterface.OnClickListener { dialog, which ->
                    endTime = System.currentTimeMillis()
                    var durationTime = (endTime - iniTime) / 1000

                    strokesViewModel.saveStrokesInEvent(it, which, strokes, durationTime)
                    areStrokesSaved = true
                    disconnectBleConnection()
                    onBackPressed()
                })
                builder.show()
            }
        })
    }

    /**
     * Allows to show a pie chart with all the strokes in real time.
     *
     */
    private fun showPieChart(){
        val entries = mutableListOf(
            PieEntry(strokes.forehand.toFloat(), StrokeType.DERECHA.toString()),
            PieEntry(strokes.backhand.toFloat(), StrokeType.REVÉS.toString()),
            PieEntry(strokes.serve.toFloat(), StrokeType.SAQUE.toString())
        )

        //Eliminar las entradas con valor de 0
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
        binding.pieChart.centerText = "Golpeos"
        binding.pieChart.setCenterTextSize(18f)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setDrawEntryLabels(true)
        binding.pieChart.setTouchEnabled(true)
        binding.pieChart.invalidate()
    }

    /**
     * Allows to set up the recyclerView needed to show the strokes by text.
     *
     */
    private fun setupRecyclerView() {
        strokeList = mutableListOf()
        adapter = StrokesAdapter(strokeList)
        binding.recyclerView.adapter = adapter

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.isNestedScrollingEnabled = false

        val animator = binding.recyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    /**
     * Allows to get all the events names.
     *
     */
    private fun endStrokeSession(){
        strokesViewModel.getEventsNames()
    }
}