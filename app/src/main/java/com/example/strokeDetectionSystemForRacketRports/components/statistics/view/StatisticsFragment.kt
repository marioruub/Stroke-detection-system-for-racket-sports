package com.example.strokeDetectionSystemForRacketRports.components.statistics.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.strokeDetectionSystemForRacketRports.databinding.FragmentStatisticsBinding
import com.example.strokeDetectionSystemForRacketRports.components.statistics.viewmodel.StatisticsViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint

/**
 * This class contains the functionality related to the view of the statistics fragment.
 *
 */
@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var statisticsViewModel: StatisticsViewModel

    /**
     * This override method is used to start the fragment.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statisticsViewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
    }

    /**
     * This override method is used to configure the layout of the UI.
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStatisticsBinding.inflate(layoutInflater)

        setUpObservers()

        showLineChart(binding.lineChart1)
        showLineChart(binding.lineChart2)
        showLineChart(binding.lineChart3)

        statisticsViewModel.getFisrtLineChartData()
        statisticsViewModel.getSecondLineChartData()
        statisticsViewModel.getThirdLineChartData()

        return binding.root
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers(){
        statisticsViewModel.lineChart1.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it != null){
                val matches = it.subList(0, 12)
                val trainings = it.subList(12, 24)

                addTwoEntries("Partidos", ArrayList(matches), Color.rgb(148, 217, 124),
                    "Entrenamientos", ArrayList(trainings), Color.rgb(250, 211, 90),
                    binding.lineChart1) // Sesiones / mes
                binding.lineChart1.visibility = View.VISIBLE
            }
        })

        statisticsViewModel.lineChart2.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it != null){
                addOneEntry("Golpeos", it, Color.rgb(148, 217, 124), binding.lineChart2) // Golpeos realizados / mes
                binding.lineChart2.visibility = View.VISIBLE
            }
        })

        statisticsViewModel.lineChart3.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it != null){
                addOneEntry("Calorías", it, Color.rgb(148, 217, 124), binding.lineChart3) // Calorías gastadas / mes
                binding.lineChart3.visibility = View.VISIBLE
            }
        })

        statisticsViewModel.lineChart3Message.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.lineChart3.visibility = View.INVISIBLE
            binding.noUserDataMessage.visibility = View.VISIBLE
        })
    }

    /**
     * Allows to show a line chart.
     *
     */
    private fun showLineChart(lineChart: LineChart){
        lineChart.setBackgroundColor(Color.WHITE)
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(false)

        val yAxis = lineChart.axisLeft
        yAxis.setDrawLabels(true)
        yAxis.setDrawAxisLine(true)
        yAxis.setDrawGridLines(false)
        val yAxisRight = lineChart.axisRight
        yAxisRight.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(12, true)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    1 -> "Ene"
                    2 -> "Feb"
                    3 -> "Mar"
                    4 -> "Abr"
                    5 -> "May"
                    6 -> "Jun"
                    7 -> "Jul"
                    8 -> "Ago"
                    9 -> "Sep"
                    10 -> "Oct"
                    11 -> "Nov"
                    12 -> "Dic"
                    else -> ""
                }
            }
        }
    }

    /**
     * Allows to add two entries in the line chart.
     *
     */
    private fun addTwoEntries(event1: String, entries1: ArrayList<Entry>, color1: Int, event2: String, entries2: ArrayList<Entry>, color2: Int, lineChart: LineChart){
        val dataSets = mutableListOf<ILineDataSet>()

        val dataSet1 = LineDataSet(entries1, event1)
        dataSet1.color = color1
        dataSet1.setCircleColor(color1)
        dataSet1.lineWidth = 4f
        dataSet1.valueTextSize = 10f
        dataSet1.valueTextColor = color1

        dataSets.add(dataSet1)

        val dataSet2 = LineDataSet(entries2, event2)
        dataSet2.color = color2
        dataSet2.setCircleColor(color2)
        dataSet2.lineWidth = 4f
        dataSet2.valueTextSize = 10f
        dataSet2.valueTextColor = color2

        dataSets.add(dataSet2)

        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    /**
     * Allows to add one entry in the line chart.
     *
     */
    private fun addOneEntry(event: String, entries: ArrayList<Entry>?, color: Int, lineChart: LineChart){
        val dataSet = LineDataSet(entries, event)
        dataSet.color = color
        dataSet.setCircleColor(color)
        dataSet.lineWidth = 4f
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor = color

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}