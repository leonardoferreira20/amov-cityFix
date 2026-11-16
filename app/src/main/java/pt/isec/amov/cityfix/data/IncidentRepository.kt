package pt.isec.amov.cityfix.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentsList
import pt.isec.amov.cityfix.data.model.closed
import pt.isec.amov.cityfix.utils.isClosingDue
import pt.isec.amov.cityfix.utils.reopenDeadline

class IncidentRepository {
    private val collection = Firebase.firestore.collection("incidents")

    suspend fun loadIncidents(): IncidentsList {
        val incidents = try {
            collection.get().await().toObjects(Incident::class.java)
        } catch (_: Exception) {
            emptyList()
        }
        return IncidentsList(incidents).also { closeDueIncidents(it) }
    }

    fun observeIncidents(): Flow<IncidentsList> = callbackFlow {
        val registration = collection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = IncidentsList(snapshot.toObjects(Incident::class.java))
                closeDueIncidents(list)
                trySend(list)
            }
        }
        awaitClose { registration.remove() }
    }

    fun saveIncident(incident: Incident) {
        collection.document(incident.id).set(incident)
    }

    private fun closeDueIncidents(list: IncidentsList) {
        val now = System.currentTimeMillis()
        list.rawList().filter { isClosingDue(it, now) }.forEach { incident ->
            reopenDeadline(incident)?.let { closedAt ->
                val closed = incident.closed(closedAt)
                list.upsert(closed)
                saveIncident(closed)
            }
        }
    }
}
