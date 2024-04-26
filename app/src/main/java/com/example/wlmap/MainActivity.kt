package com.example.wlmap

import android.R
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
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
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.ScreenBox
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_main)

        // Create a vertical LinearLayout to hold the MapView and buttons
        val container = RelativeLayout(this)

        val mapView = MapView(this)
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
        val options = listOf("Select","All","Room", "Bathroom", "Staircase", "Elevator")
        // Create a Spinner
        val spinner = Spinner(this)

        // Set up the adapter for the Spinner
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
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
        params.setMargins(16.dpToPx(), 16.dpToPx(), 60.dpToPx(), 16.dpToPx())
        spinner.layoutParams = params
        // Add the Spinner to the layout
        container.addView(spinner)
        var layerNum = 0
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (layerNum == 0){
                    return
                }
                if (options[position] == "All"){
                    if (layerNum == 3){
                        mapView.mapboxMap.getStyle { style ->
                            val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                            // Update layer properties
                            layerf3?.fillOpacity(0.8)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            symbolLayer?.filter(Expression.neq(Expression.literal(""), Expression.literal("")))
                            symbolLayer?.textField(
                                Expression.get("name"), // Existing text
                            )
                            layerf3?.fillColor("#808080")
                            symbolLayer?.textColor(Color.parseColor("#000000"))
                        }
                    }else if (layerNum == 1){
                        mapView.mapboxMap.getStyle { style ->
                            val layerf1 = style.getLayerAs<FillLayer>(FLOOR1_LAYOUT)
                            // Update layer properties
                            layerf1?.fillOpacity(0.8)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR1_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            symbolLayer?.textAllowOverlap(true)
                            symbolLayer?.filter(Expression.neq(Expression.literal(""), Expression.literal("")))
                            symbolLayer?.textField(
                                Expression.get("name"), // Existing text
                            )
                            layerf1?.fillColor("#808080")
                            symbolLayer?.textColor(Color.parseColor("#000000"))
                        }
                    }
                }else if (options[position] == "Room") {
                    if (layerNum == 3){
                        mapView.mapboxMap.getStyle { style ->
                            val layerf3 = style.getLayerAs<FillLayer>(FLOOR3_LAYOUT)
                            // Update layer properties
                            layerf3?.fillOpacity(0.8)
                            val symbolLayer = style.getLayerAs<SymbolLayer>(FlOOR3_LABELS)
                            symbolLayer?.textOpacity(1.0)
                            layerf3?.fillColor(
                                Expression.match(
                                    Expression.get("type"), // Attribute to match
                                    Expression.literal("room"), Expression.color(Color.parseColor("#A020F0")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.textAllowOverlap(true)
//                            symbolLayer?.textField(Expression.concat(
//                                Expression.get("name"), // Existing text
//                                Expression.literal(" room") // Additional string
//                            ))
                            symbolLayer?.filter(Expression.eq(Expression.literal("room"), Expression.get("type")))
                        }
                    }else if (layerNum == 1){
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
                                    Expression.literal("room"), Expression.color(Color.parseColor("#A020F0")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                )
                            )
//                            symbolLayer?.textField(Expression.concat(
//                                Expression.get("name"), // Existing text
//                                Expression.literal(" ROOM") // Additional string
//                            ))
                            symbolLayer?.filter(Expression.eq(Expression.literal("room"), Expression.get("type")))

                        }
                    }

                }else if(options[position] == "Bathroom"){
                    if (layerNum == 3){
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
                                    Expression.literal("bathroom"), Expression.color(Color.parseColor("#006400")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("bathroom"), Expression.get("type")))

                        }
                    }else if (layerNum == 1){
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
                                    Expression.literal("bathroom"), Expression.color(Color.parseColor("#006400")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("bathroom"), Expression.get("type")))

                        }
                    }
                }else if(options[position] == "Staircase"){
                    if (layerNum == 3){
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
                                    Expression.literal("stairwell"), Expression.color(Color.parseColor("#ADD8E6")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("stairwell"), Expression.get("type")))
                        }
                    }else if (layerNum == 1){
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
                                    Expression.literal("stairwell"), Expression.color(Color.parseColor("#ADD8E6")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("stairwell"), Expression.get("type")))
                        }
                    }
                }else if(options[position] == "Elevator"){
                    if (layerNum == 3){
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
                                    Expression.literal("elevator"), Expression.color(Color.parseColor("#C4A484")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                                )
                            )
                            symbolLayer?.filter(Expression.eq(Expression.literal("elevator"), Expression.get("type")))
                        }
                    }else if (layerNum == 1){
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
                                    Expression.literal("elevator"), Expression.color(Color.parseColor("#C4A484")), // Color for "room" polygons
                                    Expression.color(Color.parseColor("#808080")) // Default color for other polygons
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
        }

        fun calculateCentroid(polygon: Polygon): Point {
            var area = 0.0
            var centroidLat = 0.0
            var centroidLon = 0.0

            // Assuming polygon.coordinates() returns a list of lists of points
            val ring = polygon.coordinates()[0]
            return  polygon.coordinates()[0][0]

        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // This method will be called when the user submits the query (e.g., by pressing Enter)
                // You can perform your desired action here
                var sourceLayerId = ""
                var sourceLabelLayerId = ""
                if (layerNum == 1) {
                    sourceLayerId = FLOOR1_LAYOUT
                    sourceLabelLayerId = FlOOR1_LABELS
                }else if (layerNum == 3){
                    sourceLayerId = FLOOR3_LAYOUT
                    sourceLabelLayerId = FlOOR3_LABELS

                }
                mapView.mapboxMap.getStyle { style ->
                    val layer = style.getLayerAs<FillLayer>(sourceLayerId)
                    val source = layer?.sourceId
                    // Update layer properties
                    layer?.fillOpacity(0.8)
                    val symbolLayer = style.getLayerAs<SymbolLayer>(sourceLabelLayerId)
                    symbolLayer?.textOpacity(1.0)
                    symbolLayer?.textAllowOverlap(true)
                    layer?.fillColor(
                        Expression.match(
                            Expression.get("name"), // Attribute to match
                            Expression.literal(query), Expression.color(Color.parseColor("#39ff14")), // Color for "room" polygons
                            Expression.color(Color.parseColor("#808080")) // Default color for other polygons
                        )
                    )
                }

                mapView.mapboxMap.setCamera(
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
                            mapView.mapboxMap.setCamera(
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
            if (!isSearchViewFocused) {
                // If the search view is not focused, collapse it
                searchView.isIconified = true
            }
            // Hide the keyboard regardless of the focus state
            hideKeyboard(this, mapView)
            // Convert the geographic coordinates to screen coordinates
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
                                val finalString = restOfTheString.replace("\"", "").replace(",",", ").replace(":",": ")
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
                    stop{
                        literal(14)
                        literal(1)
                    }
                    stop{
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
                    stop{
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
                    stop{
                        literal(14)
                        literal(1)
                    }
                    stop{
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
                    stop{
                        literal(22)
                        literal(30)
                    }
                })
            }
        }

    }


    companion object {
        private const val STYLE_CUSTOM = "asset://style.json"
        private const val FLOOR1_LAYOUT = "davis01"
        private const val FLOOR3_LAYOUT = "davis03"
        private const val FlOOR1_LABELS = "davis01labels"
        private const val FlOOR3_LABELS = "davis03labels"
        private const val LATITUDE = 43.0028
        private const val LONGITUDE = -78.7873
        private const val ZOOM = 18.0
    }

    private fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }
}