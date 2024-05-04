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
import androidx.appcompat.app.AppCompatActivity
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
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import org.eclipse.paho.client.mqttv3.MqttException
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {
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

    private lateinit var mqttHandler: MqttHandler
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var circleAnnotationManager: CircleAnnotationManager
    private lateinit var polylineAnnotationManager: PolylineAnnotationManager
    private lateinit var mapView: MapView
    private lateinit var buttonF1: Button
    private lateinit var buttonF3: Button
    private lateinit var popupWindow: PopupWindow
    private lateinit var userLastLocation: Point

    private var circleAnnotationId: CircleAnnotation? = null
    private var lastLocation: Pair<Double, Double>? = null
    private var floorSelected: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
    }

    private fun onMapReady() {
        // To start the MQTT Handler -- You must have:
        // 1. Server containers launched
        // 2. Connection to UB VPN or UB network
        //initMQTTHandler()

        // Create a RelativeLayout to hold the MapView
        val container = RelativeLayout(this)
        mapView = MapView(this)

        // Start user LocationPuck plotting on and launching user NavigationRouting mapView
        userLocationPuck()

        // Initialize mapView to Davis Hall and set parameters
        initMapView()

        // Set ContentView to the RelativeLayout container
        container.addView(mapView)
        setContentView(container)

        // Initialize navigation directions popup
        initNavigationPopup()

        // Initializing floor selector and adding to ContentView container
        val floorLevelButtons = initFloorSelector()
        container.addView(floorLevelButtons)

        // Initializing drop down spinner and adding to ContentView container
        val spinner = initRoomSelector()
        container.addView(spinner)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (floorSelected == 0) {
                    return
                }
                if (spinnerOptions[position] == "All") {
                    if (floorSelected == 3) {
                        mapView.mapboxMap.getStyle { style ->
                            val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                            // Update layer properties
                            layerf3?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR3_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR3_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            symbolLayer?.filter(
                                Expression.neq(
                                    Expression.literal(""),
                                    Expression.literal("")
                                )
                            )
                            symbolLayer?.textField(
                                Expression.get("name"), // Existing text
                            )
                            layerf3?.fillColor("#7e7c77")
                            symbolLayer?.textColor(Color.parseColor("#000000"))
                        }
                    }else if (floorSelected == 1){
                        mapView.mapboxMap.getStyle { style ->
                            val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                            // Update layer properties
                            layerf1?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            symbolLayer?.filter(Expression.neq(Expression.literal(""), Expression.literal("")))
                            symbolLayer?.textField(
                                Expression.get("name"), // Existing text
                            )
                            layerf1?.fillColor("#7e7c77")
                            symbolLayer?.textColor(Color.parseColor("#000000"))
                        }
                    }
                } else if (spinnerOptions[position] == "Room") {
                    if (floorSelected == 3) {
                        mapView.mapboxMap.getStyle { style ->
                            val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                            // Update layer properties
                            layerf3?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR3_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR3_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            layerf3?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("room"), Expression.color(Color.parseColor("#A020F0")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.textAllowOverlap(true)
//                            symbolLayer?.textField(Expression.concat(
//                                Expression.get("name"), // Existing text
//                                Expression.literal(" room") // Additional string
//                            ))
                            symbolLayer?.filter(Expression.eq(Expression.literal("room"), Expression.get("type")))
                        }
                    }else if (floorSelected == 1){
                        mapView.mapboxMap.getStyle { style ->
                            val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                            // Update layer properties
                            layerf1?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            layerf1?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("room"), Expression.color(Color.parseColor("#A020F0")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                                )
                            )
//                            symbolLayer?.textField(Expression.concat(
//                                Expression.get("name"), // Existing text
//                                Expression.literal(" ROOM") // Additional string
//                            ))
                            symbolLayer?.filter(Expression.eq(Expression.literal("room"), Expression.get("type")))

                        }
                    }

                }else if(spinnerOptions[position] == "Bathroom"){
                    if (floorSelected == 3){
                        mapView.mapboxMap.getStyle { style ->
                            val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                            // Update layer properties
                            layerf3?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR3_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR3_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            layerf3?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("bathroom"), Expression.color(Color.parseColor("#006400")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("bathroom"), Expression.get("type")))

                        }
                    } else if (floorSelected == 1) {
                        mapView.mapboxMap.getStyle { style ->
                            val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                            // Update layer properties
                            layerf1?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            layerf1?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("bathroom"), Expression.color(Color.parseColor("#006400")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("bathroom"), Expression.get("type")))

                        }
                    }
                } else if (spinnerOptions[position] == "Staircase") {
                    if (floorSelected == 3) {
                        mapView.mapboxMap.getStyle { style ->
                            val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                            // Update layer properties
                            layerf3?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR3_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR3_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            layerf3?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("stairwell"), Expression.color(Color.parseColor("#ADD8E6")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("stairwell"), Expression.get("type")))
                        }
                    } else if (floorSelected == 1) {
                        mapView.mapboxMap.getStyle { style ->
                            val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                            // Update layer properties
                            layerf1?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            layerf1?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("stairwell"), Expression.color(Color.parseColor("#ADD8E6")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("stairwell"), Expression.get("type")))
                        }
                    }
                } else if (spinnerOptions[position] == "Elevator") {
                    if (floorSelected == 3) {
                        mapView.mapboxMap.getStyle { style ->
                            val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                            // Update layer properties
                            layerf3?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR3_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR3_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            layerf3?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("elevator"), Expression.color(Color.parseColor("#C4A484")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("elevator"), Expression.get("type")))
                        }
                    } else if (floorSelected == 1) {
                        mapView.mapboxMap.getStyle { style ->
                            val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                            // Update layer properties
                            layerf1?.fillOpacity(0.8)
                            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
                            doorLayer?.iconOpacity(1.0)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            layerf1?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("elevator"), Expression.color(Color.parseColor("#C4A484")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("elevator"), Expression.get("type")))
                        }
                    }
                }
            }

        }

        //searchbar

        val searchView = SearchView(this)
        val layoutParams = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        searchView.queryHint = "Search Room Name"
        searchView.isIconifiedByDefault = false
        searchView.setBackgroundColor(Color.DKGRAY)
        // change color
        val id = searchView.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        val closeButtonId = searchView.context.resources
            .getIdentifier("android:id/search_close_btn", null, null)
        val magnifyId = searchView.context.resources.getIdentifier("android:id/search_mag_icon",null,null)
        val magnifyView = searchView.findViewById<View>(magnifyId) as ImageView
        magnifyView.setColorFilter(Color.WHITE)
        val buttonView = searchView.findViewById<View>(closeButtonId) as ImageView
        buttonView.setColorFilter(Color.WHITE)
        val textView = searchView.findViewById<View>(id) as TextView
        textView.setTextColor(Color.WHITE)
        textView.setHintTextColor(Color.WHITE)
        // Add the Searchbar to your layout
        container.addView(searchView, layoutParams)

        // Keep track of whether the search view is focused
        var isSearchViewFocused = false
        // Set an OnFocusChangeListener to the search view
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            isSearchViewFocused = hasFocus
        }

        fun hideKeyboard(context: Context, view: View) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            searchView.clearFocus()
        }

        fun calculateCentroid(polygon: Polygon): Point {
            Log.d("DEBUG",polygon.coordinates().toString())
            Log.d("DEBUG2",polygon.coordinates().size.toString())
            return  polygon.coordinates()[0][0]

        }

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // The search bar has gained focus, recenter the map
                mapView.mapboxMap.flyTo(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                        .pitch(0.0)
                        .zoom(ZOOM)
                        .bearing(0.0)
                        .build()
                )
            }
        }

        var userQuery = ""

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                userQuery = query
                // This method will be called when the user submits the query (e.g., by pressing Enter)
                // You can perform your desired action here
                var sourceLayerId = ""
                var sourceLabelLayerId = ""
                var sourceLayerDoorId = ""
                if (floorSelected == 1) {
                    sourceLayerId = FLOOR1_LAYOUT
                    sourceLabelLayerId = FLOOR1_LABELS
                    sourceLayerDoorId = FLOOR1_DOORS
                }else if (floorSelected == 3){
                    sourceLayerId = FLOOR3_LAYOUT
                    sourceLabelLayerId = FLOOR3_LABELS
                    sourceLayerDoorId = FLOOR3_DOORS
                }
                mapView.mapboxMap.getStyle { style ->
                    val layer = style.getLayerAs<FillLayer>(sourceLayerId)
                    // Update layer properties
                    layer?.fillOpacity(0.8)
                    val doorLayer = style.getLayerAs<SymbolLayer>(sourceLayerDoorId)
                    doorLayer?.iconOpacity(1.0)
                    val symbolLayer = style.getLayerAs<SymbolLayer>(sourceLabelLayerId)
                    symbolLayer?.textOpacity(1.0)
                    symbolLayer?.textAllowOverlap(true)
                    layer?.fillColor(
                        Expression.match(
                            Expression.get("name"), // Attribute to match
                            Expression.literal(query), Expression.color(Color.parseColor("#39ff14")), // Color for "room" polygons
                            Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                        )
                    )
                }

                mapView.mapboxMap.flyTo(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                        .pitch(0.0)
                        .zoom(ZOOM)
                        .bearing(0.0)
                        .build()
                )
                val visibleBounds = mapView.mapboxMap.coordinateBoundsForCamera(CameraOptions.Builder()
                    .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                    .pitch(0.0)
                    .zoom(ZOOM)
                    .bearing(0.0)
                    .build())

                val screenPoint1 = mapView.mapboxMap.pixelForCoordinate(visibleBounds.northwest())
                val screenPoint2 = mapView.mapboxMap.pixelForCoordinate(visibleBounds.southeast())
                val visibleAreaPolygon = ScreenBox(screenPoint1, screenPoint2)


                // Create a RenderedQueryGeometry from the visible area geometry
                val renderedQueryGeometry = RenderedQueryGeometry(visibleAreaPolygon)
                val renderedQueryOptions = RenderedQueryOptions(listOf(sourceLayerId), Expression.eq(Expression.get("name"), Expression.literal(query)))
                mapView.mapboxMap.queryRenderedFeatures(renderedQueryGeometry,renderedQueryOptions) { features ->
                    if (features.isValue) {
                        val f = features.value
                        if (f != null && f.size > 0) {
                            val room = f[0].queriedFeature.feature.geometry() as Polygon
                            Log.d("DEBUG",f[0].queriedFeature.feature.getProperty("name").toString())
                            val center = calculateCentroid(room)
                            mapView.mapboxMap.flyTo(
                                CameraOptions.Builder()
                                    .center(center)
                                    .pitch(0.0)
                                    .zoom(20.0)
                                    .bearing(0.0)
                                    .build()
                            )
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // This method will be called when the text in the search view changes
                // You can implement any filtering logic here if needed
                return false
            }
        })



        mapView.mapboxMap.addOnMapClickListener { point ->
            var sourceLayerId = ""
            var sourceLabelLayerId = ""
            var sourceLayerDoorId = ""

            //publishLocation(point)


            if (!isSearchViewFocused) {
                // If the search view is not focused, collapse it
                searchView.isIconified = true
            }

            // Hide the keyboard regardless of the focus state
            hideKeyboard(this, mapView)

            // Reset userQuery room color from search bar
            mapView.mapboxMap.getStyle { style ->
                if (floorSelected == 1) {
                    sourceLayerId = FLOOR1_LAYOUT
                    sourceLabelLayerId = FLOOR1_LABELS
                    sourceLayerDoorId = FLOOR1_DOORS
                }else if (floorSelected == 3) {
                    sourceLayerId = FLOOR3_LAYOUT
                    sourceLabelLayerId = FLOOR3_LABELS
                    sourceLayerDoorId = FLOOR3_DOORS
                }

                val layer = style.getLayerAs<FillLayer>(sourceLayerId)
                // Update layer properties
                layer?.fillOpacity(0.8)

                val doorLayer = style.getLayerAs<SymbolLayer>(sourceLayerDoorId)
                doorLayer?.iconOpacity(1.0)

                val symbolLayer = style.getLayerAs<SymbolLayer>(sourceLabelLayerId)
                symbolLayer?.textOpacity(1.0)

                symbolLayer?.textAllowOverlap(true)

                layer?.fillColor(
                    Expression.match(
                        Expression.get("name"), // Attribute to match
                        Expression.literal(userQuery), Expression.color(Color.parseColor("#7e7c77")), // Color for "room" polygons
                        Expression.color(Color.parseColor("#7e7c77")) // Default color for other polygons
                    )
                )
            }

            // Convert the geographic coordinates to screen coordinates
            val screenPoint = mapView.mapboxMap.pixelForCoordinate(point)
            val renderedQueryGeometry = RenderedQueryGeometry(screenPoint)
            val currentLayer = floorSelected
            if (currentLayer != 0){
                if (currentLayer == 3){
                    sourceLayerId = FLOOR3_LAYOUT
                }else if(currentLayer == 1){
                    sourceLayerId = FLOOR1_LAYOUT
                }
                val renderedQueryOptions = RenderedQueryOptions(listOf(sourceLayerId), Expression.neq(Expression.literal(""), Expression.literal("")))
                mapView.mapboxMap.queryRenderedFeatures(renderedQueryGeometry,renderedQueryOptions) { features->
                    if (features.isValue){
                        val f = features.value
                        if (f != null && f.size > 0) {
                            val featureString = f[0].toString()
                            Log.d("DEBUG", featureString)
                            val propertiesIndex = featureString.indexOf("properties")
                            if (propertiesIndex != -1) {
                                var restOfTheString = featureString.substring(propertiesIndex+12)
                                val bracketIndex = restOfTheString.indexOf("}")
                                if (bracketIndex != -1) {
                                    restOfTheString = restOfTheString.substring(0, bracketIndex)
                                }
                                val finalString = restOfTheString.replace("\"", "").replace(",",", ").replace(":",": ")
                                Toast.makeText(this@MainActivity, finalString, Toast.LENGTH_SHORT ).show()

                                // Directions navigation popup on room click
                                val x = screenPoint.x.toInt()
                                val y = screenPoint.y.toInt()

                                popupWindow.showAtLocation(searchView, Gravity.NO_GRAVITY, x, y)
                            }
//                        val toast = Toast.makeText(this@MainActivity, print_m, Toast.LENGTH_LONG).show()
                        } else {

                            /*
                            mapView.mapboxMap.flyTo(
                                CameraOptions.Builder()
                                    .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                                    .pitch(0.0)
                                    .zoom(ZOOM)
                                    .bearing(0.0)
                                    .build()
                            )
                             */

                        }
                    }
                }
            }
            true // Return true to consume the click event
        }

        buttonF1.setOnClickListener {
            floorSelected = 1
            mapView.mapboxMap.getStyle { style ->
                // Get an existing layer by referencing its
                // unique layer ID (LAYER_ID)
                val layer = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                // Update layer properties
                layer?.fillOpacity(0.0)
                val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR3_DOORS)
                doorLayer?.iconOpacity(0.0)
                // Add symbol layer for floor 3 labels
                val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR3_LABELS)
                symbolLayer?.textOpacity(0.0)
            }
            mapView.mapboxMap.getStyle { style ->
                // Get an existing layer by referencing its
                // unique layer ID (LAYER_ID)
                val layer = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                // Update layer properties
                layer?.fillOpacity(0.8)
                val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
                doorLayer?.iconOpacity(0.8)
                val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
                symbolLayer?.textOpacity(1.0)
                symbolLayer?.textAllowOverlap(true)
                symbolLayer?.textFont(
                    listOf("DIN Offc Pro Bold") // Specify the font family with bold weight
                )
                symbolLayer?.textSize(Expression.interpolate {
                    exponential {
                        literal(2)
                    }
                    zoom()
                    stop {
                        literal(14)
                        literal(1)
                    }
                    stop {
                        literal(16)
                        literal(5)
                    }
                    stop {
                        literal(18)
                        literal(7)
                    }
                    stop {
                        literal(20)
                        literal(20)
                    }
                    stop {
                        literal(22)
                        literal(30)
                    }
                })
            }
        }


        buttonF3.setOnClickListener {
            floorSelected = 3
            mapView.mapboxMap.getStyle { style ->
                // Get an existing layer by referencing its
                // unique layer ID (LAYER_ID)
                val layer = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                // Update layer properties
                layer?.fillOpacity(0.0)
                val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
                doorLayer?.iconOpacity(0.0)
                val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
                symbolLayer?.textOpacity(0.0)
            }
            mapView.mapboxMap.getStyle { style ->
                // Get an existing layer by referencing its
                // unique layer ID (LAYER_ID)
                val layer = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                // Update layer properties
                layer?.fillOpacity(0.8)
                val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR3_DOORS)
                doorLayer?.iconOpacity(1.0)
                val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR3_LABELS)
                symbolLayer?.textOpacity(1.0)
                symbolLayer?.textAllowOverlap(true)
                symbolLayer?.textFont(
                    listOf("DIN Offc Pro Bold") // Specify the font family with bold weight
                )
                symbolLayer?.textSize(Expression.interpolate {
                    exponential {
                        literal(2)
                    }
                    zoom()
                    stop {
                        literal(14)
                        literal(1)
                    }
                    stop {
                        literal(16)
                        literal(5)
                    }
                    stop {
                        literal(18)
                        literal(7)
                    }
                    stop {
                        literal(20)
                        literal(20)
                    }
                    stop {
                        literal(22)
                        literal(30)
                    }
                })
            }
        }
    }

    private fun userLocationPuck() { 
        val annotationApi = mapView.annotations
        val circleAnnotationManager = annotationApi.createCircleAnnotationManager()

        val locationService : LocationService = LocationServiceFactory.getOrCreate()
        val locationProvider: DeviceLocationProvider?

        val request = LocationProviderRequest.Builder()
            .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
            .displacement(2F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build();

        val result = locationService.getDeviceLocationProvider(request)
        locationProvider = result.value

        val locationObserver = LocationObserver { locations ->
            Log.e(ContentValues.TAG, "Location update received: $locations.")
            // Assuming you want to plot the first location received
            val location = updateLocation(locations[0].latitude,locations[0].longitude)
            val point = Point.fromLngLat(location.second,location.first)
            Log.e(ContentValues.TAG, "Location update received: $location")

            // Set options for the resulting circle layer.
            val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()

                // Define a geographic coordinate.
                .withPoint(point)

                // Style the circle that will be added to the map.
                .withCircleRadius(8.0)
                .withCircleColor("#4a90e2")
                .withCircleStrokeWidth(3.5)
                .withCircleStrokeColor("#FAF9F6")

            if (circleAnnotationManager.annotations.contains(circleAnnotationId)) {
                // Delete the previous LocationPuck annotation
                circleAnnotationManager.delete(circleAnnotationId!!)
            }

            // Store last location for nav routing algorithm
            userLastLocation = point

            // Create and add the new circle annotation to the map
            circleAnnotationId = circleAnnotationManager.create(circleAnnotationOptions)
        }

        locationProvider?.addLocationObserver(locationObserver)
    }

    private fun initNavigationPopup() {
        // Create the button programmatically with an icon next to the text
        val button = Button(this).apply {
            text = "Get Directions"
            // Set the icon to the left of the text
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_menu_directions, 0)
            setCompoundDrawablePadding(10) // Sets the padding to 10 pixels
            setOnClickListener {
                Toast.makeText(this@MainActivity, "Navigating...", Toast.LENGTH_SHORT).show()
                userNavigationRouting()
                popupWindow.dismiss()
            }
        }

        // Initialize the PopupWindow (assuming you have a PopupWindow instance)
        popupWindow = PopupWindow(this).apply {
            width = LinearLayout.LayoutParams.WRAP_CONTENT
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            isFocusable = true
            contentView = button
            setBackgroundDrawable(null)
        }
    }

    private fun initRoomSelector(): Spinner {
        // Create a Spinner
        val spinner = Spinner(this)

        // Set up the adapter for the Spinner
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, spinnerOptions)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = object :
            ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, spinnerOptions) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(R.id.text1)
                textView.setTextColor(Color.WHITE) // Set the desired text color here
                view.setBackgroundColor(Color.DKGRAY)
                return view
            }
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(R.id.text1)
                textView.setTextColor(Color.WHITE) // Set the desired text color here
                view.setBackgroundColor(Color.DKGRAY)
                return view
            }

        }

        // Set layout parameters for the Spinner
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        params.addRule(RelativeLayout.ALIGN_PARENT_TOP) // Align to the top
        params.addRule(RelativeLayout.ALIGN_PARENT_END) // Align to the end (right)
        params.setMargins(16.dpToPx(), 16.dpToPx(), 60.dpToPx(), 16.dpToPx())

        spinner.layoutParams = params

        return spinner
    }

    private fun initFloorSelector(): LinearLayout {
        // Create a LinearLayout to hold the buttons
        val floorLevelButtons = LinearLayout(this)
        floorLevelButtons.id = View.generateViewId() // Generate a unique id for the LinearLayout
        val paramsButtons = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsButtons.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        paramsButtons.addRule(RelativeLayout.ALIGN_PARENT_END)
        paramsButtons.setMargins(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 80.dpToPx())
        floorLevelButtons.orientation = LinearLayout.VERTICAL
        floorLevelButtons.layoutParams = paramsButtons

        // Create and add buttons to the LinearLayout
        buttonF1 = Button(this)
        buttonF1.id = View.generateViewId() // Generate a unique id for the button
        buttonF1.text = "1"
        buttonF1.setBackgroundColor(Color.DKGRAY)
        buttonF1.setTextColor(Color.WHITE)
        val buttonParams1 = LinearLayout.LayoutParams(
            50.dpToPx(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams1.gravity = Gravity.END
        buttonF1.layoutParams = buttonParams1
        floorLevelButtons.addView(buttonF1)

        buttonF3 = Button(this)
        buttonF3.id = View.generateViewId() // Generate a unique id for the button
        buttonF3.text = "3"
        buttonF3.setBackgroundColor(Color.DKGRAY)
        buttonF3.setTextColor(Color.WHITE)
        val buttonParams2 = LinearLayout.LayoutParams(
            50.dpToPx(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams2.gravity = Gravity.END
        buttonF3.layoutParams = buttonParams2
        floorLevelButtons.addView(buttonF3)

        return floorLevelButtons
    }

    private fun initMapView() {
        // Enable gestures
        mapView.gestures.doubleTapToZoomInEnabled = true
        mapView.gestures.rotateEnabled = true
        mapView.gestures.pinchToZoomEnabled = true

        // Load custom on-device style
        mapView.mapboxMap.loadStyle(style = STYLE_CUSTOM)

        // Get and load the style for floor 1 of Davis Hall
        mapView.mapboxMap.getStyle { style ->
            val layer = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
            layer?.fillOpacity(0.0)

            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
            doorLayer?.iconOpacity(0.0)

            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
            symbolLayer?.textOpacity(0.0)
        }

        // Get and load styles for floor 3 of Davis Hall
        mapView.mapboxMap.getStyle { style ->
            val layer = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
            layer?.fillOpacity(0.0)

            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR3_DOORS)
            doorLayer?.iconOpacity(0.0)

            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR3_LABELS)
            symbolLayer?.textOpacity(0.0)
        }

        // Set camera position to Davis Hall
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                .pitch(0.0)
                .zoom(ZOOM)
                .bearing(0.0)
                .build()
        )
    }

    private fun userNavigationRouting() {
        val annotationApi = mapView.annotations
        circleAnnotationManager = annotationApi.createCircleAnnotationManager()
        polylineAnnotationManager = annotationApi.createPolylineAnnotationManager()


        /*
        //
        Point.fromLngLat(-78.7868938249454,43.00285069822394),
        Point.fromLngLat(-78.78751469317484,43.00285069822394),
        Point.fromLngLat(-78.78751469317484, 43.00265668269077),
        Point.fromLngLat(-78.78751469317484, 43.002701127103336),
        Point.fromLngLat(-78.78756781748096, 43.002701127103336)

         */



        // List of walkable points of floor 1
        val f1Walk = listOf(
            // HALL C117
            Point.fromLngLat(-78.78751469317484, 43.00265668269077),
            Point.fromLngLat(-78.78742202301328, 43.00265668269077),
            Point.fromLngLat(-78.78734166862753, 43.00265668269077),
            Point.fromLngLat(-78.78724505207666, 43.00265668269077),
            Point.fromLngLat(-78.78715369602253,43.00265668269077),
            Point.fromLngLat(-78.78705246522863, 43.00265668269077),
            Point.fromLngLat(-78.78692752615112, 43.00265668269077),

            // HALL C116
            Point.fromLngLat(-78.78689428375111, 43.00265668269077),
            Point.fromLngLat(-78.78689428375111, 43.00273843797882),
            Point.fromLngLat(-78.78689428375111, 43.002830102156025),
            Point.fromLngLat(-78.78689428375111, 43.00285059380832),

            //HALL C115 LEFT
            Point.fromLngLat(-78.78695554257372, 43.00285059380832),
            Point.fromLngLat(-78.78704131765394, 43.00285059380832),
            Point.fromLngLat(-78.78708934724371, 43.00285059380832),
            Point.fromLngLat(-78.78719371883047, 43.00285059380832),
            Point.fromLngLat(-78.78724589506906, 43.00285059380832),
            Point.fromLngLat(-78.78734442483108, 43.00285059380832),
            Point.fromLngLat(-78.78742256376356, 43.00285059380832),
            Point.fromLngLat(-78.78746825354492, 43.00285059380832),

            //HALL C115 RIGHT // BATHROOM NODE Point.fromLngLat(-78.78766072255355, 43.00285059380832),
            Point.fromLngLat(-78.78751469317484, 43.00285059380832),
            Point.fromLngLat(-78.78765887996539, 43.00285059380832),
            Point.fromLngLat(-78.78775558522321, 43.00285059380832),

            //MAIN HALL 101
            //Point.fromLngLat(-78.78751469317484, 43.00265668269077),
            Point.fromLngLat(-78.78751469317484, 43.002776797223675),
            Point.fromLngLat(-78.78756988740314, 43.002776797223675),
            Point.fromLngLat(-78.78751469317484, 43.00270086610047),
            Point.fromLngLat(-78.78755328875651,43.00270086610047),


            //SIDE ENTRANCE BY 101 -78.78755927097362
            Point.fromLngLat(-78.78755328875651, 43.002534795993796),
            Point.fromLngLat(-78.78755328875651, 43.00242066370484),
            Point.fromLngLat(-78.78766689749101, 43.00242066370484),
            Point.fromLngLat(-78.78773364869417, 43.00242066370484)
        )


        /*
        // Set options for the resulting line layer.
        val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(f1Walk)
            // Style the line that will be added to the map.
            .withLineColor("#0f53ff")
            .withLineWidth(6.3)
            .withLineJoin(LineJoin.ROUND)
            .withLineSortKey(0.0)

        // Add the resulting line to the map.
        polylineAnnotationManager.create(polylineAnnotationOptions)
        */

        mapView.mapboxMap.getStyle { style ->
            // Get an existing layer by referencing its
            // unique layer ID (LAYER_ID)
            val layer = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
            // Update layer properties
            layer?.fillOpacity(1.0)
            val doorLayer = style.getLayerAs<SymbolLayer>(FLOOR1_DOORS)
            doorLayer?.iconOpacity(0.0)
            // Add symbol layer for floor 3 labels
            val symbolLayer = style.getLayerAs<SymbolLayer>(FLOOR1_LABELS)
            symbolLayer?.textOpacity(1.0)
        }

        // Circle at each point in 'f1Walk'
        for (point in f1Walk) {
            // Create a circle marker for each point
            val circleMarkerOptions:CircleAnnotationOptions = CircleAnnotationOptions()
                .withPoint(point)
                .withCircleColor("#000000") // Match the color with the polyline
                .withCircleRadius(5.0) // Set the radius of the circle
                .withCircleOpacity(1.0) // Set the opacity of the circle
                .withCircleSortKey(1.0) // Ensure the circle is drawn above the polyline

            // Add the circle marker to the map
            circleAnnotationManager.create(circleMarkerOptions)
        }


        /*
        val closestPoint = Graph.findClosestPoint(f1Walk,userLastLocation)


        val circleMarkerOptions:CircleAnnotationOptions = CircleAnnotationOptions()
            .withPoint(closestPoint)
            .withCircleColor("#ff0000") // Match the color with the polyline
            .withCircleRadius(5.0) // Set the radius of the circle
            .withCircleOpacity(1.0) // Set the opacity of the circle
            .withCircleSortKey(1.0) // Ensure the circle is drawn above the polyline



        // Add the circle marker to the map
        circleAnnotationManager.create(circleMarkerOptions)
         */

    }

    private fun updateLocation(newLatitude: Double, newLongitude: Double): Pair<Double, Double> {
        if (lastLocation == null) {
            lastLocation = Pair(newLatitude, newLongitude)
            return lastLocation!!
        }
        val alpha = 0.1 // Smoothing factor
        val latitude = lastLocation!!.first + alpha * (newLatitude - lastLocation!!.first)
        val longitude = lastLocation!!.second + alpha * (newLongitude - lastLocation!!.second)
        lastLocation = Pair(latitude, longitude)
        return lastLocation!!
    }

    private fun initMQTTHandler() {
        mqttHandler = MqttHandler()
        mqttHandler.connect(serverUri, clientId)
        mqttHandler.subscribe(serverTopic)
        mqttHandler.onMessageReceived = { message ->
            runOnUiThread {
                Log.e("SERVER", message)
            }
        }
    }
    private fun publishLocation(point: Point) {
        val lat = point.latitude()
        val long = point.longitude()
        val serverMessage = "ack,$long,$lat"
        mqttHandler.publish("test/topic",serverMessage)
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            mqttHandler.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }
}