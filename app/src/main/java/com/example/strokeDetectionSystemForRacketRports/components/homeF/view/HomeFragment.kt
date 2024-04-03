package com.example.strokeDetectionSystemForRacketRports.components.homeF.view

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.databinding.FragmentHomeBinding
import com.example.strokeDetectionSystemForRacketRports.components.homeF.viewmodel.HomeFViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * This class contains the functionality related to the view of the home fragment.
 *
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeFViewModel

    /**
     * This override method is used to start the fragment.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeFViewModel::class.java)
    }

    /**
     * This override method is used to configure the layout of the UI.
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        setUpListeners()
        setUpObservers()
        setUpUserImage()

        showUserInfo()
        showDateText()
        getEvents()

        return binding.root
    }

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners(){
        binding.settingsButtom.setOnClickListener { showSettingsFragment() }
    }

    /**
     * Allows to set up all the observers used to connect the view with the viewModel.
     *
     */
    private fun setUpObservers(){
        homeViewModel.userImageUrl.observe(viewLifecycleOwner, Observer {
            if (it != Uri.EMPTY){
                Glide.with(this)
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.userImage)
                binding.userImage.clipToOutline = true
            }
        })

        homeViewModel.userTopData.observe(viewLifecycleOwner, Observer {
            if(it.isEmpty()){
                binding.textName.text = "Nombre"
                binding.numPartidosText.text = "0 partidos jugados"
            }
            else{
                binding.textName.text = it[0]
                binding.numPartidosText.text = it[1] + " partidos jugados"
            }
        })

        homeViewModel.matchesHomePage.observe(viewLifecycleOwner, Observer{
            if(it.isNotEmpty())
                showEvents(it, Color.rgb(148, 217, 124))
        })

        homeViewModel.trainingsHomePage.observe(viewLifecycleOwner, Observer{
            if(it.isNotEmpty())
                showEvents(it, Color.rgb(250, 211, 90))
        })

        homeViewModel.otherEventsHomePage.observe(viewLifecycleOwner, Observer{
            if(it.isNotEmpty())
                showEvents(it, Color.rgb(114, 156, 247))
        })
    }

    /**
     * Allows to show the settings fragment.
     *
     */
    private fun showSettingsFragment(){
        val bundle = Bundle()
        bundle.putString("email", arguments?.getString("email"))

        val settingsFragment = SettingsFragment()
        settingsFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, settingsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Allows to set up the user profile image.
     *
     */
    private fun setUpUserImage(){
        Glide.with(this)
            .load(R.drawable.user)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.userImage)
        binding.userImage.clipToOutline = true

        homeViewModel.getUserProfileImage()
    }

    /**
     * Allows to show the information of an user.
     *
     */
    private fun showUserInfo(){
        homeViewModel.getUserInfo()
    }

    /**
     * Allows to show the text related to each events in the home fragment.
     *
     */
    private fun showEventText(eventName: String, color: Int, layOut: LinearLayout){
        val textView = TextView(context)
        textView.text = eventName
        textView.textSize = 16f
        textView.setTextColor(Color.BLACK)
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setPadding(20, 20, 20, 20)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 0, 0)
        textView.layoutParams = layoutParams


        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(color)
        gradientDrawable.cornerRadius = 20f
        textView.background = gradientDrawable

        layOut.addView(textView)
    }

    /**
     * Allows to show the dates for the next seven days.
     *
     */
    private fun showDateText(){
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = getCurrentDate(formatter)

        val textViewList = arrayListOf(binding.secondDayText, binding.thirdDayText, binding.fourthDayText, binding.fifthDayText, binding.sixthDayText, binding.seventhDayText)

        for(i in 0 until textViewList.size){
            textViewList[i].text = formatter.format(currentDate.time + (i+1) * 24 * 60 * 60 * 1000)
        }
    }

    /**
     * Allows to get all the events for the next seven days.
     *
     */
    private fun getEvents(){
        homeViewModel.getMatchesForNextSevenDays()
        homeViewModel.getTrainingsForNextSevenDays()
        homeViewModel.getOtherEventsForNextSevenDays()
    }

    /**
     * Allows to get the current date.
     *
     */
    private fun getCurrentDate(formatter: SimpleDateFormat): Date {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return formatter.parse(String.format("%02d/%02d/%04d", dayOfMonth, month, year))
    }

    /**
     * Allows to show the events names in the home fragment.
     *
     */
    private fun showEvents(it: ArrayList<ArrayList<String>>,color: Int){
        val textViewList = arrayListOf(binding.firstDayLayOut, binding.secondDayLayOut, binding.thirdDayLayOut, binding.fourthDayLayOut, binding.fifthDayLayOut, binding.sixthDayLayOut, binding.seventhDayLayOut)
        for((j, events) in it.withIndex()){
            for(i in 0 until events.size){
                showEventText(events[i], color, textViewList[j])
            }
        }
    }
}