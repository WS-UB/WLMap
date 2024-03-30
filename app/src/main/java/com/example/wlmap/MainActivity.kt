package com.example.wlmap

import android.R
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
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
        paramsButtons.setMargins(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
        floorLevelButtons.orientation = LinearLayout.VERTICAL
        floorLevelButtons.layoutParams = paramsButtons

        // Create and add buttons to the LinearLayout
        val button_f1 = Button(this)
        button_f1.id = View.generateViewId() // Generate a unique id for the button
        button_f1.text = "1"
        button_f1.setBackgroundColor(ContextCompat.getColor(this, R.color.system_surface_bright_dark))
        button_f1.setTextColor(ContextCompat.getColor(this, R.color.white))
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
        button_f3.setBackgroundColor(ContextCompat.getColor(this, R.color.system_surface_bright_dark))
        button_f3.setTextColor(ContextCompat.getColor(this, R.color.white))
        val buttonParams2 = LinearLayout.LayoutParams(
            50.dpToPx(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams2.gravity = Gravity.END
        button_f3.layoutParams = buttonParams2
        floorLevelButtons.addView(button_f3)

        // Add the LinearLayout with buttons to the container
        container.addView(floorLevelButtons)


        // Set the content view to the container
        setContentView(container)

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
            }
        }


        button_f3.setOnClickListener {
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
        private const val ZOOM = 17.9
    }

    private fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }
}