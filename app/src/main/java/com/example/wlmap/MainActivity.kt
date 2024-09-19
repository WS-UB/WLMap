package com.example.wlmap

import LocationPermissionHelper
import android.R
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.ScreenBox
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.toCameraOptions
import org.eclipse.paho.client.mqttv3.MqttException
import java.lang.ref.WeakReference
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : AppCompatActivity()
,NavigationView.OnNavigationItemSelectedListener
{
    private val serverUri = "tcp://128.205.218.189:1883"
    private val clientId = "Client ID"
    private val serverTopic = "receive-wl-map"
    private val STYLE_CUSTOM = "asset://style.json"
    private val FLOOR1_LAYOUT = "davis01"
    private val FLOOR1_LABELS = "davis01labels"
    private val FLOOR1_DOORS = "davis01doors"
    private val FLOOR3_LABELS = "davis03labels"
    private val FLOOR3_LAYOUT = "davis03"
    private val FLOOR3_DOORS = "davis03doors"
    private val spinnerOptions = listOf("Select", "All", "Room", "Bathroom", "Staircase", "Elevator")
    private val LATITUDE = 43.0028
    private val LONGITUDE = -78.7873
    private val ZOOM = 17.9
    private val testUserLocation = Point.fromLngLat(-78.78755328875651, 43.002534795993796)

    private lateinit var mqttHandler: MqttHandler
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var annotationAPI: AnnotationPlugin
    private lateinit var userAnnotationManager: CircleAnnotationManager
    private lateinit var doorAnnotationManager: CircleAnnotationManager
    private lateinit var polylineAnnotationManager: PolylineAnnotationManager
    private lateinit var mapView: MapView
    private lateinit var buttonF1: Button
    private lateinit var buttonF3: Button
    private lateinit var popupWindow: PopupWindow
    private lateinit var userLastLocation: Point
    private lateinit var drawerLayout: DrawerLayout

    //private var curRoute: List<Point> = null
    private var doorSelected: Point? = null
    private var circleAnnotationId: CircleAnnotation? = null
    private var prevRoute: PolylineAnnotation? = null
    private var routeDisplayed: Boolean = false
    private var prevDoor: Boolean = false
    private var lastLocation: Pair<Double, Double>? = null
    private var floorSelected: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
//             ! Call the function that runs the WLMap application app.
//             onMapReady()
        }

        // Set the layout for the activity. This loads the 'activity_main' layout resource.
        setContentView(com.example.wlmap.R.layout.activity_main)

        // Find the DrawerLayout by its ID and assign it to 'drawerLayout'.
        drawerLayout = findViewById<DrawerLayout>(com.example.wlmap.R.id.drawer_layout)

        // Find the Toolbar by its ID and assign it to 'toolbar'.
        val toolbar = findViewById<Toolbar>(com.example.wlmap.R.id.toolbar)

        // Set the toolbar as the app's ActionBar, allowing it to function like a standard action bar.
        setSupportActionBar(toolbar)

        // Find the NavigationView (the menu within the drawer) by its ID and assign it to 'homeScreen'.
        // The 'setNavigationItemSelectedListener' sets the current activity as the listener for menu item selections.
        val homeScreen = findViewById<NavigationView>(com.example.wlmap.R.id.nav_view)
        homeScreen.setNavigationItemSelectedListener(this)

        // Create an ActionBarDrawerToggle to handle the opening and closing of the navigation drawer.
        // The toggle binds the drawer, the activity (this), and the toolbar, and sets labels for open and close states.
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, com.example.wlmap.R.string.open_home, com.example.wlmap.R.string.close_home)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // Check if the activity is being recreated (such as after a screen rotation) by checking if 'savedInstanceState' is null.
        // If it's null, it means the activity is newly created, so we should initialize the home fragment and set the default menu item.
        if (savedInstanceState == null) {
            // Replace the current fragment with the HomeFragment.
            replaceFragment(HomeFragment())
            // Set the "Home" menu item as checked by default in the navigation drawer.
            homeScreen.setCheckedItem(com.example.wlmap.R.id.nav_home)
        }
    }

    // This function handles navigation item selections from a navigation drawer.
    // It overrides the 'onNavigationItemSelected' method of the NavigationView.OnNavigationItemSelectedListener interface.
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            // If the "Home" item is selected, replace the current fragment with 'HomeFragment'.
            com.example.wlmap.R.id.nav_home -> supportFragmentManager.beginTransaction().replace(com.example.wlmap.R.id.fragment_container, HomeFragment()).commit()

            // TODO: Figure out a way to create a WLMap Fragment
            // If the "WLMap" item is selected, we run the interactive map.
            com.example.wlmap.R.id.nav_wlmap -> supportFragmentManager.beginTransaction().replace(com.example.wlmap.R.id.fragment_container, MapFragment()).commit()

            // If the "Data" item is selected, replace the current fragment with 'SendDataFragment'.
            com.example.wlmap.R.id.nav_data -> supportFragmentManager.beginTransaction().replace(com.example.wlmap.R.id.fragment_container, SendDataFragment()).commit()

            // If the "Settings" item is selected, replace the current fragment with 'SettingsFragment'.
            com.example.wlmap.R.id.nav_setting -> supportFragmentManager.beginTransaction().replace(com.example.wlmap.R.id.fragment_container, SettingsFragment()).commit()

            // If the "Logout" item is selected, show a Toast message saying "Logout!".
            com.example.wlmap.R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // A private method that replaces the current fragment with the provided HomeFragment.
    private fun replaceFragment(fragment: HomeFragment){
        // Begin a new fragment transaction.
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        // Replace the fragment currently in the 'fragment_container' with the given 'fragment'.
        transaction.replace(com.example.wlmap.R.id.fragment_container, fragment)

        // Commit the transaction to apply the changes.
        transaction.commit()
    }

    // Override the 'onBackPressed' method to handle the back button behavior.
    override fun onBackPressed() {
        // Call the parent class's 'onBackPressed' method to perform any default back press behavior.
        super.onBackPressed()

        // Check if the navigation drawer is open on the start (left) side of the screen.
        // If so, we close it.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        // If not, handle the back press as normal using 'onBackPressedDispatcher'.
        else{
            onBackPressedDispatcher.onBackPressed()
        }
    }

}