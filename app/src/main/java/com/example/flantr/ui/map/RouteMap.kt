package com.example.flantr.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.flantr.data.model.Stop
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun RouteMap(
    stops: List<Stop>,
    routePoints: List<GeoPoint>, // The detailed trail
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Initialize OSM
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize(),
        update = { map ->
            map.overlays.clear()

            // 1. User Location (Blue Dot)
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
            locationOverlay.enableMyLocation()
            map.overlays.add(locationOverlay)

            // 2. Draw the Trail
            if (routePoints.isNotEmpty()) {
                val line = Polyline().apply {
                    setPoints(routePoints)
                    outlinePaint.color = android.graphics.Color.parseColor("#8B5CF6") // Purple
                    outlinePaint.strokeWidth = 15f
                }
                map.overlays.add(line)
            }

            // 3. Draw Pins for Stops
            stops.forEachIndexed { index, stop ->
                stop.geoPoint?.let { gp ->
                    val marker = Marker(map)
                    marker.position = GeoPoint(gp.lat, gp.lng)
                    marker.title = "${index + 1}. ${stop.name}"
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    // Allows you to click the pin to see the name
                    map.overlays.add(marker)
                }
            }

            // Center map on first stop if available
            if (stops.isNotEmpty() && stops[0].geoPoint != null) {
                val start = stops[0].geoPoint!!
                map.controller.setCenter(GeoPoint(start.lat, start.lng))
            }

            map.invalidate()
        }
    )

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }
}