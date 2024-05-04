package com.example.wlmap

import com.mapbox.geojson.Point
import com.mapbox.maps.logD
import org.w3c.dom.Node
import kotlin.math.*



object Graph {
    private val nodes = mutableMapOf<String, Node>()
    private val edges = mutableMapOf<String, Node>()
    private class Node(val point: NavPoint) {
        val neighbors = mutableMapOf<Node, Double>()
    }

    fun addNode(point: NavPoint) {
        nodes[point.id] = Node(point)
    }

    fun addEdge(start: NavPoint, end: NavPoint, dist: Double) {
        edges
    }

    fun haversine(userLocation: Point, walkPoint: Point): Double {
        // Convert decimal degrees to radians
        val lon1Rad = Math.toRadians(userLocation.longitude())
        val lat1Rad = Math.toRadians(userLocation.latitude())
        val lon2Rad = Math.toRadians(walkPoint.longitude())
        val lat2Rad = Math.toRadians(walkPoint.latitude())

        // Haversine formula
        val dlon = lon2Rad - lon1Rad
        val dlat = lat2Rad - lat1Rad
        val a = sin(dlat / 2).pow(2.0) + cos(lat1Rad) * cos(lat2Rad) * sin(dlon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return 6371 * c
    }

    fun initNodes(points: List<Point>) {

    }

    fun findClosestPoint(points: List<Point>, userLocation: Point): Point {
        lateinit var closestPoint: Point
        var minDistance = Double.MAX_VALUE

        for (point in points) {
            val curDistance = haversine(userLocation,point)
            if (curDistance < minDistance) {
                minDistance = curDistance
                closestPoint = point
            }
        }

        return closestPoint
    }

    fun routeGeneration() {

    }


}