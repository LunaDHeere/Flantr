package com.example.flantr.utils

import android.location.Location
import com.example.flantr.data.model.GeoPoint

object LocationUtils {

    // Calculate distance in meters between user and a stop
    fun getDistanceMeters(startLat: Double, startLng: Double, end: GeoPoint): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat, startLng, end.lat, end.lng, results)
        return results[0]
    }

    // Calculate ETA based on preferences
    fun calculateEtaMinutes(
        distanceMeters: Float,
        walkingPace: String, // "slow", "moderate", "fast"
        includePublicTransport: Boolean
    ): Int {
        // Average walking speed ~ 83 meters/min (5 km/h)
        var speed = 83f

        // Adjust for pace
        speed *= when (walkingPace.lowercase()) {
            "slow" -> 0.8f
            "fast" -> 1.2f
            else -> 1.0f
        }

        // Simple heuristic: if using public transport for long distances (>1km),
        // assume 3x speed (bus/tram) (since i don't use google maps api)
        if (includePublicTransport && distanceMeters > 1000) {
            speed *= 3.0f
        }

        return (distanceMeters / speed).toInt()
    }
}