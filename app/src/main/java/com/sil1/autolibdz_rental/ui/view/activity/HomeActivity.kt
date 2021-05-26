package com.sil1.autolibdz_rental.ui.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.sil1.autolibdz_rental.R
import com.sil1.autolibdz_rental.databinding.ActivityHomeBinding
import com.sil1.autolibdz_rental.ui.view.fragment.map_display.MapDisplayFragment
import java.util.*


class HomeActivity : AppCompatActivity(){
 //   private lateinit var mMap: GoogleMap
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize( applicationContext, getString(R.string.api_key));
        }
        var placesClient = Places.createClient(this)

        //var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val menuButton: FloatingActionButton = binding.appBarHome.menuButton
        val drawerLayout: DrawerLayout = binding.drawerLayout

        menuButton.setOnClickListener{
            // If the navigation drawer is not open then open it, if its already open then close it.
            if(!drawerLayout.isDrawerOpen(Gravity.START)) drawerLayout.openDrawer(Gravity.START);
            else drawerLayout.closeDrawer(Gravity.END);
        }

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profil, R.id.nav_history, R.id.nav_transaction, R.id.nav_reclamation, R.id.nav_assistance
            ), drawerLayout
        )
        navView.setupWithNavController(navController)
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}