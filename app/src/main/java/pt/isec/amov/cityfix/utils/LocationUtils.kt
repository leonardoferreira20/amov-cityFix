package pt.isec.amov.cityfix.utils

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import pt.isec.amov.cityfix.data.model.GeoLocation

object LocationUtils {

    fun hasLocationPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    fun locationUpdates(context: Context, intervalMillis: Long = 10_000L): Flow<GeoLocation> =
        callbackFlow {
            if (!hasLocationPermission(context)) {
                close()
                return@callbackFlow
            }
            val client = LocationServices.getFusedLocationProviderClient(context)
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { trySend(GeoLocation(it.latitude, it.longitude)) }
                }
            }
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
                .setMinUpdateIntervalMillis(intervalMillis / 2)
                .build()
            try {
                client.lastLocation.addOnSuccessListener { location ->
                    location?.let { trySend(GeoLocation(it.latitude, it.longitude)) }
                }
                client.requestLocationUpdates(request, callback, Looper.getMainLooper())
            } catch (e: SecurityException) {
                close(e)
            }
            awaitClose { client.removeLocationUpdates(callback) }
        }

    fun isLocationEnabled(context: Context): Boolean =
        context.getSystemService(LocationManager::class.java)
            ?.let { LocationManagerCompat.isLocationEnabled(it) } == true

    fun checkLocationSettings(
        context: Context,
        onEnabled: () -> Unit,
        onResolutionRequired: (IntentSenderRequest) -> Unit
    ) {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L).build())
            .build()
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(request)
            .addOnSuccessListener { onEnabled() }
            .addOnFailureListener { error ->
                if (error is ResolvableApiException) {
                    onResolutionRequired(IntentSenderRequest.Builder(error.resolution).build())
                }
            }
    }

    fun hasBackgroundLocationPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    fun openDirections(context: Context, location: GeoLocation) {
        val coordinates = "${location.latitude},${location.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$coordinates?q=$coordinates"))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$coordinates")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    fun distanceMeters(from: GeoLocation, to: GeoLocation): Float {
        val results = FloatArray(1)
        Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, results)
        return results[0]
    }
}
