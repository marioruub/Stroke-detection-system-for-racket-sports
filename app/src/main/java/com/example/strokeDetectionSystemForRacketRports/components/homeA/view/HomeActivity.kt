package com.example.strokeDetectionSystemForRacketRports.components.homeA.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.databinding.ActivityHomeBinding
import com.example.strokeDetectionSystemForRacketRports.components.authentication.view.LogInActivity
import com.example.strokeDetectionSystemForRacketRports.components.strokes.view.BleConectionFragment
import com.example.strokeDetectionSystemForRacketRports.components.homeF.view.HomeFragment
import com.example.strokeDetectionSystemForRacketRports.components.homeF.viewmodel.SettingsFragmentListener
import com.example.strokeDetectionSystemForRacketRports.components.events.view.AllEventsFragment
import com.example.strokeDetectionSystemForRacketRports.components.homeA.viewmodel.NavMenuFragmentListener
import com.example.strokeDetectionSystemForRacketRports.components.homeA.viewmodel.NavMenuType
import com.example.strokeDetectionSystemForRacketRports.components.homeA.viewmodel.HomeAViewModel
import com.example.strokeDetectionSystemForRacketRports.components.statistics.view.StatisticsFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * This class contains the functionality related to the view of the home activity.
 * It is where all the fragments are instantiated.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavMenuFragmentListener, SettingsFragmentListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeAViewModel

    /**
     * This override method is used to start the activity and set up the user interface.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = ViewModelProvider(this).get(HomeAViewModel::class.java)

        saveSession()

        showHomeFragment()
    }

    /**
     * This override method is called when the user pressed the back button.
     *
     */
    override fun onBackPressed(){} //Sobreescribir el metodo para que no se pueda volver a la anterior pestaña

    /**
     * This override method is called when the user pressed a button from the menu.
     *
     */
    override fun onMenuItemSelected(menuItem: NavMenuType) {
        removeFragment()
        when(menuItem){
            NavMenuType.HOME -> showHomeFragment()
            NavMenuType.MATCHES -> showEventsFragment()
            NavMenuType.STATISTICS -> showStatisticsFragment()
            NavMenuType.STROKES -> showBLEFragment()
        }
    }

    /**
     * This override method is used to log out an user account.
     *
     */
    override fun logOut() {
        deleteSession()
        homeViewModel.logOutUser() //Cerrar sesión usuario
        showLogInActivity() //Ir a la pestaña de logIn
    }

    /**
     * Allows to save an user session.
     *
     */
    private fun saveSession(){
        val email = intent.getStringExtra("email")

        val prefs = getSharedPreferences("com.example.mytennisapp10.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()
    }

    /**
     * Allows to delete an user session.
     *
     */
    private fun deleteSession(){
        val prefs = getSharedPreferences("com.example.mytennisapp10.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
    }

    /**
     * Allows to show the logIn activity.
     *
     */
    private fun showLogInActivity(){
        val logInIntent = Intent(this, LogInActivity::class.java)
        startActivity(logInIntent)
    }

    /**
     * Allows to remove a fragment.
     *
     */
    private fun removeFragment(){
        val fragmentManager = supportFragmentManager
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
        bundle.putString("email", intent.getStringExtra("email"))

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle
        fragmentTransaction.add(R.id.fragmentContainer, homeFragment)
        fragmentTransaction.commit()
    }

    /**
     * Allows to show the bleConnection fragment.
     *
     */
    private fun showBLEFragment(){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val bleConectionFragment = BleConectionFragment()
        fragmentTransaction.add(R.id.fragmentContainer, bleConectionFragment)
        fragmentTransaction.commit()
    }

    /**
     * Allows to show the allEvents fragment.
     *
     */
    private fun showEventsFragment(){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val allEventsFragment = AllEventsFragment()
        fragmentTransaction.add(R.id.fragmentContainer, allEventsFragment)
        fragmentTransaction.commit()
    }

    /**
     * Allows to show the statistics fragment.
     *
     */
    private fun showStatisticsFragment(){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val calendarFragment = StatisticsFragment()
        fragmentTransaction.add(R.id.fragmentContainer, calendarFragment)
        fragmentTransaction.commit()
    }
}