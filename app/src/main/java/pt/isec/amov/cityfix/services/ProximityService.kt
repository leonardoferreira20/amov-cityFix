package pt.isec.amov.cityfix.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.CityFixApp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.withProximityAlert
import pt.isec.amov.cityfix.utils.LocationUtils
import pt.isec.amov.cityfix.utils.PROXIMITY_RADIUS_METERS
import pt.isec.amov.cityfix.utils.isProximityAlertTarget

class ProximityService : Service() {
    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val repository by lazy { (applicationContext as CityFixApp).incidentRepository }
    private val notifiedIncidents = mutableSetOf<String>()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var operatorId: String? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { checkProximity(GeoLocation(it.latitude, it.longitude)) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle(getString(R.string.proximity_service_title))
            .setContentText(getString(R.string.proximity_service_text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        } else {
            0
        }
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, type)
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(EXTRA_OPERATOR_ID)?.let { operatorId = it }
        return START_STICKY
    }

    private fun startLocationUpdates() {
        if (!LocationUtils.hasLocationPermission(this)) {
            stopSelf()
            return
        }
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 15_000L)
            .setMinUpdateIntervalMillis(10_000L)
            .build()
        try {
            locationClient.requestLocationUpdates(request, locationCallback, mainLooper)
        } catch (_: SecurityException) {
            stopSelf()
        }
    }

    private fun checkProximity(operatorLocation: GeoLocation) = serviceScope.launch {
        val operator = operatorId ?: return@launch
        repository.loadIncidents().rawList().forEach { incident ->
            val isNear = isProximityAlertTarget(incident) &&
                LocationUtils.distanceMeters(operatorLocation, incident.location) <= PROXIMITY_RADIUS_METERS
            if (isNear) {
                if (notifiedIncidents.add(incident.id)) {
                    repository.saveIncident(incident.withProximityAlert(operator, System.currentTimeMillis()))
                }
            } else {
                notifiedIncidents.remove(incident.id)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.proximity_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "proximity_channel"
        private const val NOTIFICATION_ID = 1337
        private const val EXTRA_OPERATOR_ID = "operator_id"

        fun intent(context: Context, operatorId: String): Intent =
            Intent(context, ProximityService::class.java).putExtra(EXTRA_OPERATOR_ID, operatorId)
    }
}
