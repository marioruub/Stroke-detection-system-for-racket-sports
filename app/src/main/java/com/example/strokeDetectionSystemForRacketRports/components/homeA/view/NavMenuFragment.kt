package com.example.strokeDetectionSystemForRacketRports.components.homeA.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.components.homeA.viewmodel.NavMenuFragmentListener
import com.example.strokeDetectionSystemForRacketRports.components.homeA.viewmodel.NavMenuType
import com.example.strokeDetectionSystemForRacketRports.databinding.FragmentNavMenuBinding

/**
 * This class contains the functionality related to the view of the menu fragment.
 *
 */
class NavMenuFragment : Fragment() {
    private lateinit var binding: FragmentNavMenuBinding
    private lateinit var menuListener: NavMenuFragmentListener

    /**
     * This override method is used to start the fragment.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuListener = context as NavMenuFragmentListener
    }

    /**
     * This override method is used to configure the layout of the UI.
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNavMenuBinding.inflate(layoutInflater)

        setUpListeners()

        return binding.root
    }

    /**
     * Allows to set up all the listeners used to collect all the events related with the view.
     *
     */
    private fun setUpListeners(){
        binding.navMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    menuListener.onMenuItemSelected(NavMenuType.HOME)
                    true
                }
                R.id.games -> {
                    menuListener.onMenuItemSelected(NavMenuType.MATCHES)
                    true
                }
                R.id.statistics -> {
                    menuListener.onMenuItemSelected(NavMenuType.STATISTICS)
                    true
                }
                R.id.strokes -> {
                    menuListener.onMenuItemSelected(NavMenuType.STROKES)
                    true
                }
                else -> false
            }
        }
    }
}