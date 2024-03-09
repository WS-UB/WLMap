package com.example.wlmap

import android.R
import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.plugin.gestures.gestures


class MainActivity : AppCompatActivity() {

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


        // Set the content view to the container
        setContentView(container)

        mapView.gestures.doubleTapToZoomInEnabled = true
        mapView.gestures.rotateEnabled = true
        mapView.gestures.pinchToZoomEnabled = true

        mapView.mapboxMap.loadStyle(style = STYLE_CUSTOM)

    }

    companion object {
        private const val STYLE_CUSTOM = "asset://style.json"
        private const val FLOOR1_LAYOUT = "davis01-poly-46dzby"
        private const val FLOOR3_LAYOUT = "davis03-aef7zh"
        private const val LATITUDE = 43.0028
        private const val LONGITUDE = -78.7873
        private const val ZOOM = 17.5
    }

    private fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }
}