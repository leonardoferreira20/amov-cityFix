package pt.isec.amov.cityfix.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.utils.isOperator

class DeviceTokenRepository {
    private val collection = Firebase.firestore.collection("deviceTokens")
    private val messaging = Firebase.messaging

    suspend fun register(user: User) {
        try {
            saveToken(user.id, messaging.token.await())
            if (isOperator(user)) {
                messaging.subscribeToTopic(OPERATORS_TOPIC).await()
            } else {
                messaging.unsubscribeFromTopic(OPERATORS_TOPIC).await()
            }
        } catch (_: Exception) {
        }
    }

    fun saveToken(userId: String, token: String) {
        collection.document(userId).set(mapOf("token" to token, "updatedAt" to System.currentTimeMillis()))
    }

    fun unregister() {
        messaging.deleteToken()
    }

    companion object {
        const val OPERATORS_TOPIC = "operators"
    }
}
