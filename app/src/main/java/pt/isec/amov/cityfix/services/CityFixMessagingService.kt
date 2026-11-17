package pt.isec.amov.cityfix.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pt.isec.amov.cityfix.CityFixApp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.ui.components.labelRes
import pt.isec.amov.cityfix.utils.IncidentNotifications

class CityFixMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        val app = application as CityFixApp
        app.authRepository.currentUserId?.let { app.deviceTokenRepository.saveToken(it, token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val incidentId = data[KEY_INCIDENT_ID] ?: return
        val category = data[KEY_CATEGORY].orEmpty()
        val (title, text) = when (data[KEY_TYPE]) {
            TYPE_STATUS_CHANGED -> {
                val status = IncidentStatus.entries.find { it.name == data[KEY_STATUS] } ?: return
                R.string.notification_status_title to
                    getString(R.string.notification_status_text, category, getString(status.labelRes()))
            }
            TYPE_INFO_REQUESTED -> R.string.notification_request_title to
                getString(R.string.notification_request_text, category)
            TYPE_MESSAGE -> R.string.notification_message_title to
                getString(R.string.notification_message_text, category)
            TYPE_PROXIMITY -> R.string.notification_proximity_title to
                getString(R.string.notification_proximity_text, category)
            TYPE_CLOSING_SOON -> R.string.notification_closing_title to
                getString(R.string.notification_closing_text, category)
            else -> return
        }
        IncidentNotifications.show(this, incidentId, getString(title), text)
    }

    companion object {
        private const val KEY_TYPE = "type"
        private const val KEY_INCIDENT_ID = "incidentId"
        private const val KEY_CATEGORY = "categoryName"
        private const val KEY_STATUS = "status"
        private const val TYPE_STATUS_CHANGED = "STATUS_CHANGED"
        private const val TYPE_INFO_REQUESTED = "INFO_REQUESTED"
        private const val TYPE_MESSAGE = "MESSAGE"
        private const val TYPE_PROXIMITY = "PROXIMITY"
        private const val TYPE_CLOSING_SOON = "CLOSING_SOON"
    }
}
