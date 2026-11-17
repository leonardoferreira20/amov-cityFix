package pt.isec.amov.cityfix

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import pt.isec.amov.cityfix.data.AuthRepository
import pt.isec.amov.cityfix.data.CategoryRepository
import pt.isec.amov.cityfix.data.DeviceTokenRepository
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.MediaUploadScheduler
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.utils.IncidentNotifications

class CityFixApp : Application() {
    val authRepository by lazy { AuthRepository() }
    val incidentRepository by lazy { IncidentRepository(MediaUploadScheduler(this)) }
    val categoryRepository by lazy { CategoryRepository() }
    val userRepository by lazy { UserRepository() }
    val deviceTokenRepository by lazy { DeviceTokenRepository() }

    override fun onCreate() {
        super.onCreate()
        Firebase.firestore.firestoreSettings = firestoreSettings {
            setLocalCacheSettings(persistentCacheSettings {})
        }
        IncidentNotifications.createChannel(this)
    }
}
