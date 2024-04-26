package com.example.wlmap

import LocationPermissionHelper
import android.R
import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationError
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import org.eclipse.paho.client.mqttv3.MqttException
import java.lang.ref.WeakReference


class WLocationConsumer: LocationConsumer {
    override fun onBearingUpdated(vararg bearing: Double, options: (ValueAnimator.() -> Unit)?) {
        TODO("Not yet implemented")
    }

    override fun onError(error: LocationError) {
        TODO("Not yet implemented")
    }

    override fun onHorizontalAccuracyRadiusUpdated(
        vararg radius: Double,
        options: (ValueAnimator.() -> Unit)?
    ) {
        TODO("Not yet implemented")
    }

    override fun onLocationUpdated(vararg location: Point, options: (ValueAnimator.() -> Unit)?) {
        TODO("Not yet implemented")
    }

    override fun onPuckAccuracyRadiusAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onPuckBearingAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onPuckLocationAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
        TODO("Not yet implemented")
    }
}


class MainActivity : AppCompatActivity() {

    private val STYLE_CUSTOM = "asset://style.json"
    private val FLOOR1_LAYOUT = "davis01"
    private val FLOOR3_LAYOUT = "davis03"
    private val FlOOR1_LABELS = "davis01labels"
    private val FlOOR3_LABELS = "davis03labels"
    private val LATITUDE = 43.0028
    private val LONGITUDE = -78.7873
    private val ZOOM = 17.9

    private val serverUri = "tcp://128.205.218.189:1883"
    private val clientId = "Client ID"
    private val serverTopic = "receive-wl-map"
    private lateinit var mqttHandler: MqttHandler

    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private var circleAnnotationId: CircleAnnotation? = null
    private var lastLocation: Pair<Double, Double>? = null

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {

            startMqttHandler()

            // Create a vertical LinearLayout to hold the MapView and buttons
            val container = RelativeLayout(this)
            val mapView = MapView(this)
            val annotationApi = mapView.annotations
            val circleAnnotationManager = annotationApi.createCircleAnnotationManager()
            val polylineAnnotationManager = annotationApi.createPolylineAnnotationManager()


            val locationService : LocationService = LocationServiceFactory.getOrCreate()
            var locationProvider: DeviceLocationProvider? = null

            val request = LocationProviderRequest.Builder()
                .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
                .displacement(0F)
                .accuracy(AccuracyLevel.HIGHEST)
                .build();

            val result = locationService.getDeviceLocationProvider(request)
            locationProvider = result.value

            val locationObserver = object: LocationObserver {
                override fun onLocationUpdateReceived(locations: MutableList<Location>) {
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

                    // Removing the previous location with the new update
                    circleAnnotationId?.let {
                        circleAnnotationManager.delete(it)
                    }

                    // Create and add the new circle annotation to the map
                    circleAnnotationId = circleAnnotationManager.create(circleAnnotationOptions)
                }
            }
            locationProvider?.addLocationObserver(locationObserver)


            // Define a list of geographic coordinates to be connected.
            val points = listOf(
                Point.fromLngLat( -78.78759,43.002665),
                Point.fromLngLat( -78.78753812740018,43.00269736914555),
                Point.fromLngLat( -78.78753812740018,43.002728514107844),
                Point.fromLngLat( -78.78753957936134,43.002758621754225),
                Point.fromLngLat( -78.78753855612797,43.00279588440392),
                Point.fromLngLat( -78.78753904102501,43.00284741340417),
                Point.fromLngLat( -78.78747266477298,43.00285070834681),
                Point.fromLngLat( -78.78738743910438,43.00284966186862),
                Point.fromLngLat( -78.78738237545627,43.00286521647894)
            )

            // Set options for the resulting line layer.
            val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
                .withPoints(points)
                // Style the line that will be added to the map.
                .withLineColor("#0f53ff")
                .withLineWidth(6.3)
                .withLineJoin(LineJoin.ROUND)

            // Add the resulting line to the map.
            polylineAnnotationManager.create(polylineAnnotationOptions)

            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                    .pitch(0.0)
                    .zoom(ZOOM)
                    .bearing(0.0)
                    .build()
            )

            // Add the map view to the activity (you can also add it to other views as a child)
            //setContentView(mapView)

            container.addView(mapView)
            setContentView(container)

            // Create a LinearLayout to hold the buttons
            val floorLevelButtons = LinearLayout(this)
            floorLevelButtons.id =
                View.generateViewId() // Generate a unique id for the LinearLayout
            val paramsButtons = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            paramsButtons.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            paramsButtons.addRule(RelativeLayout.ALIGN_PARENT_END)
            paramsButtons.setMargins(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
            floorLevelButtons.orientation = LinearLayout.VERTICAL
            floorLevelButtons.layoutParams = paramsButtons

            // Create and add buttons to the LinearLayout
            val button_f1 = Button(this)
            button_f1.id = View.generateViewId() // Generate a unique id for the button
            button_f1.text = "1"
            button_f1.setBackgroundColor(Color.DKGRAY)
            button_f1.setTextColor(Color.WHITE)
            val buttonParams1 = LinearLayout.LayoutParams(
                50.dpToPx(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            buttonParams1.gravity = Gravity.END
            button_f1.layoutParams = buttonParams1
            floorLevelButtons.addView(button_f1)

            val button_f3 = Button(this)
            button_f3.id = View.generateViewId() // Generate a unique id for the button
            button_f3.text = "3"
            button_f3.setBackgroundColor(Color.DKGRAY)
            button_f3.setTextColor(Color.WHITE)
            val buttonParams2 = LinearLayout.LayoutParams(
                50.dpToPx(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            buttonParams2.gravity = Gravity.END
            button_f3.layoutParams = buttonParams2
            floorLevelButtons.addView(button_f3)

            // Add the LinearLayout with buttons to the container
            container.addView(floorLevelButtons)


            // Define the options for the dropdown
            val options = listOf("Select", "All", "Room", "Bathroom", "Staircase", "Elevator")
            // Create a Spinner
            val spinner = Spinner(this)

            // Set up the adapter for the Spinner
            val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = object :
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
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
            params.setMargins(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
            spinner.layoutParams = params

            // Add the Spinner to the layout
            container.addView(spinner)
            var layerNum = 0
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (layerNum == 0) {
                        return
                    }
                    if (options[position] == "All") {
                        if (layerNum == 3) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                                // Update layer properties
                                layerf3?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
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
                                layerf3?.fillColor("#808080")
                                symbolLayer?.textColor(Color.parseColor("#000000"))
                            }
                        } else if (layerNum == 1) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                                // Update layer properties
                                layerf1?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
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
                                layerf1?.fillColor("#808080")
                                symbolLayer?.textColor(Color.parseColor("#000000"))
                            }
                        }
                    } else if (options[position] == "Room") {
                        if (layerNum == 3) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                                // Update layer properties
                                layerf3?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
                                symbolLayer?.textOpacity(1.0)
                                layerf3?.fillColor(
                                    Expression.match(
                                        Expression.get("type"), // Attribute to match
                                        Expression.literal("room"),
                                        Expression.color(Color.parseColor("#A020F0")), // Color for "room" polygons
                                        Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                    )
                                )
                                symbolLayer?.textAllowOverlap(true)
                                symbolLayer?.filter(
                                    Expression.eq(
                                        Expression.literal("room"),
                                        Expression.get("type")
                                    )
                                )
                            }
                        } else if (layerNum == 1) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                                // Update layer properties
                                layerf1?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
                                symbolLayer?.textOpacity(1.0)
                                symbolLayer?.textAllowOverlap(true)
                                layerf1?.fillColor(
                                    Expression.match(
                                        Expression.get("type"), // Attribute to match
                                        Expression.literal("room"),
                                        Expression.color(Color.parseColor("#A020F0")), // Color for "room" polygons
                                        Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                    )
                                )
                                symbolLayer?.filter(
                                    Expression.eq(
                                        Expression.literal("room"),
                                        Expression.get("type")
                                    )
                                )

                            }
                        }

                    } else if (options[position] == "Bathroom") {
                        if (layerNum == 3) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                                // Update layer properties
                                layerf3?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
                                symbolLayer?.textOpacity(1.0)
                                symbolLayer?.textAllowOverlap(true)
                                layerf3?.fillColor(
                                    Expression.match(
                                        Expression.get("type"), // Attribute to match
                                        Expression.literal("bathroom"),
                                        Expression.color(Color.parseColor("#006400")), // Color for "room" polygons
                                        Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                    )
                                )
                                symbolLayer?.filter(
                                    Expression.eq(
                                        Expression.literal("bathroom"),
                                        Expression.get("type")
                                    )
                                )

                            }
                        } else if (layerNum == 1) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                                // Update layer properties
                                layerf1?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
                                symbolLayer?.textOpacity(1.0)
                                symbolLayer?.textAllowOverlap(true)
                                layerf1?.fillColor(
                                    Expression.match(
                                        Expression.get("type"), // Attribute to match
                                        Expression.literal("bathroom"),
                                        Expression.color(Color.parseColor("#006400")), // Color for "room" polygons
                                        Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                    )
                                )
                                symbolLayer?.filter(
                                    Expression.eq(
                                        Expression.literal("bathroom"),
                                        Expression.get("type")
                                    )
                                )

                            }
                        }
                    } else if (options[position] == "Staircase") {
                        if (layerNum == 3) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                                // Update layer properties
                                layerf3?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
                                symbolLayer?.textOpacity(1.0)
                                symbolLayer?.textAllowOverlap(true)
                                layerf3?.fillColor(
                                    Expression.match(
                                        Expression.get("type"), // Attribute to match
                                        Expression.literal("stairwell"),
                                        Expression.color(Color.parseColor("#ADD8E6")), // Color for "room" polygons
                                        Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                    )
                                )
                                symbolLayer?.filter(
                                    Expression.eq(
                                        Expression.literal("stairwell"),
                                        Expression.get("type")
                                    )
                                )
                            }
                        } else if (layerNum == 1) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                                // Update layer properties
                                layerf1?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
                                symbolLayer?.textOpacity(1.0)
                                symbolLayer?.textAllowOverlap(true)
                                layerf1?.fillColor(
                                    Expression.match(
                                        Expression.get("type"), // Attribute to match
                                        Expression.literal("stairwell"),
                                        Expression.color(Color.parseColor("#ADD8E6")), // Color for "room" polygons
                                        Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                    )
                                )
                                symbolLayer?.filter(
                                    Expression.eq(
                                        Expression.literal("stairwell"),
                                        Expression.get("type")
                                    )
                                )
                            }
                        }
                    } else if (options[position] == "Elevator") {
                        if (layerNum == 3) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                                // Update layer properties
                                layerf3?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
                                symbolLayer?.textOpacity(1.0)
                                symbolLayer?.textAllowOverlap(true)
                                layerf3?.fillColor(
                                    Expression.match(
                                        Expression.get("type"), // Attribute to match
                                        Expression.literal("elevator"),
                                        Expression.color(Color.parseColor("#C4A484")), // Color for "room" polygons
                                        Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                    )
                                )
                                symbolLayer?.filter(
                                    Expression.eq(
                                        Expression.literal("elevator"),
                                        Expression.get("type")
                                    )
                                )
                            }
                        } else if (layerNum == 1) {
                            mapView.mapboxMap.getStyle { style ->
                                val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                                // Update layer properties
                                layerf1?.fillOpacity(0.8)
                                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
                                symbolLayer?.textOpacity(1.0)
                                symbolLayer?.textAllowOverlap(true)
                                layerf1?.fillColor(
                                    Expression.match(
                                        Expression.get("type"), // Attribute to match
                                        Expression.literal("elevator"),
                                        Expression.color(Color.parseColor("#C4A484")), // Color for "room" polygons
                                        Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                    )
                                )
                                symbolLayer?.filter(
                                    Expression.eq(
                                        Expression.literal("elevator"),
                                        Expression.get("type")
                                    )
                                )
                            }
                        }
                    }
                }
            }

            mapView.mapboxMap.addOnMapClickListener { point ->

                //publishLocation(point)

                val screenPoint = mapView.mapboxMap.pixelForCoordinate(point)
                val renderedQueryGeometry = RenderedQueryGeometry(screenPoint)
                val currentLayer = layerNum
                var sourceLayerId = ""
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
                                    var finalString = restOfTheString.replace("\"", "").replace(",",", ").replace(":",": ")
                                    Toast.makeText(this@MainActivity, finalString, Toast.LENGTH_SHORT ).show()
                                    // Iterate through each character in the rest of the string
                                }
//                        val toast = Toast.makeText(this@MainActivity, print_m, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                true // Return true to consume the click event
            }



            mapView.gestures.doubleTapToZoomInEnabled = true
            mapView.gestures.rotateEnabled = true
            mapView.gestures.pinchToZoomEnabled = true

            mapView.mapboxMap.loadStyle(style = STYLE_CUSTOM)

            // Get the style
            mapView.mapboxMap.getStyle { style ->
                // Get an existing layer by referencing its
                // unique layer ID (LAYER_ID)
                val layer = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                // Update layer properties
                layer?.fillOpacity(0.0)
                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
                symbolLayer?.textOpacity(0.0)
            }

            mapView.mapboxMap.getStyle { style ->
                // Get an existing layer by referencing its
                // unique layer ID (LAYER_ID)

                val layer = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                // Update layer properties
                layer?.fillOpacity(0.0)
                val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
                symbolLayer?.textOpacity(0.0)
            }


            button_f1.setOnClickListener {
                layerNum = 1
                mapView.mapboxMap.getStyle { style ->
                    // Get an existing layer by referencing its
                    // unique layer ID (LAYER_ID)
                    val layer = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                    // Update layer properties
                    layer?.fillOpacity(0.0)
                    // Add symbol layer for floor 3 labels
                    val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
                    symbolLayer?.textOpacity(0.0)
                }
                mapView.mapboxMap.getStyle { style ->
                    // Get an existing layer by referencing its
                    // unique layer ID (LAYER_ID)
                    val layer = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                    // Update layer properties
                    layer?.fillOpacity(0.8)
                    val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
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


            button_f3.setOnClickListener {
                layerNum = 3
                mapView.mapboxMap.getStyle { style ->
                    // Get an existing layer by referencing its
                    // unique layer ID (LAYER_ID)
                    val layer = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                    // Update layer properties
                    layer?.fillOpacity(0.0)
                    val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
                    symbolLayer?.textOpacity(0.0)
                }
                mapView.mapboxMap.getStyle { style ->
                    // Get an existing layer by referencing its
                    // unique layer ID (LAYER_ID)
                    val layer = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                    // Update layer properties
                    layer?.fillOpacity(0.8)
                    val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
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
    }

    private fun startMqttHandler() {
        mqttHandler = MqttHandler()
        mqttHandler.connect(serverUri, clientId)
        mqttHandler.subscribe(serverTopic)

        mqttHandler.onMessageReceived = { message ->
            runOnUiThread {
                Log.e("SERVER", message)
            }
        }
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

    private fun publishLocation(point: Point) {
        val lat = point.latitude()
        val long = point.longitude()
        val serverMessage = "ack,$lat,$long"

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