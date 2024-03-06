package com.example.wlmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.bindgen.Value
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.rgb
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.generated.vectorSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.gestures.gestures

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapboxOptions.accessToken = "pk.eyJ1IjoiY2d2YXJnaGUiLCJhIjoiY2x0ZGNwMDEzMDVhMzJrc2hpeW42NG9kbyJ9.iLZBux7plgn3t4yA2g5YOQ"
        setContentView(R.layout.activity_main)

        // Create a map programmatically and set the initial camera
        val mapView = MapView(this)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-78.7873, 43.0028))
                .pitch(0.0)
                .zoom(17.5)
                .bearing(0.0)
                .build()
        )
        // Add the map view to the activity (you can also add it to other views as a child)
        setContentView(mapView)

        mapView.gestures.rotateEnabled = true
        mapView.gestures.pinchToZoomEnabled = true

        mapView.mapboxMap.loadStyle(
            style(style = Style.STANDARD) {
                +geoJsonSource(id = "floor-1") {
                    data("asset://floor1-davis.geojson")
                }
            }
        )
    }

}