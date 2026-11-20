package pt.isec.amov.cityfix.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.utils.IMPACT_MIN_SPEED_KMH
import pt.isec.amov.cityfix.utils.IMPACT_SUGGESTION_INTERVAL_MILLIS
import pt.isec.amov.cityfix.utils.ImpactDetector
import pt.isec.amov.cityfix.utils.IncidentNotifications
import pt.isec.amov.cityfix.utils.LocationUtils

class ImpactDetectionService : Service(), SensorEventListener {
    private val sensorManager by lazy { getSystemService(SensorManager::class.java) }
    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val detector = ImpactDetector()
    private var lastLocation: Location? = null
    private var lastSuggestionAt = 0L

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            lastLocation = result.lastLocation
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            if (!availability.isLocationAvailable) lastLocation = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle(getString(R.string.impact_service_title))
            .setContentText(getString(R.string.impact_service_text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        } else {
            0
        }
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, type)

        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null || !LocationUtils.hasLocationPermission(this)) {
            stopSelf()
            return
        }
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .build()
        try {
            locationClient.requestLocationUpdates(request, locationCallback, mainLooper)
        } catch (_: SecurityException) {
            stopSelf()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!detector.isImpact(event.values)) return
        val location = lastLocation ?: return
        val now = System.currentTimeMillis()
        if (!isMoving(location) || now - lastSuggestionAt < IMPACT_SUGGESTION_INTERVAL_MILLIS) return

        lastSuggestionAt = now
        IncidentNotifications.showImpactSuggestion(this, GeoLocation(location.latitude, location.longitude))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun isMoving(location: Location): Boolean =
        location.hasSpeed() && location.speed * 3.6f >= IMPACT_MIN_SPEED_KMH

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.impact_service_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
        locationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private companion object {
        const val CHANNEL_ID = "impact_detection_channel"
        const val NOTIFICATION_ID = 1338
    }
}
