package pt.isec.amov.cityfix.data.model

import pt.isec.amov.cityfix.utils.GROUP_RADIUS_METERS
import pt.isec.amov.cityfix.utils.LocationUtils

class IncidentsList(initialIncidents: List<Incident> = emptyList()) {
    private val _incidents = initialIncidents.toMutableList()

    fun getAllSorted(): List<Incident> = _incidents.sortedByDescending { it.createdAt }

    fun findById(id: String): Incident? = _incidents.find { it.id == id }

    fun rawList(): List<Incident> = _incidents.toList()

    fun getDashboardStats(): DashboardStats {
        val repairTimes = _incidents.mapNotNull { incident ->
            incident.closedAt?.let { it - incident.createdAt }
        }
        val activeCount = _incidents.count { it.status != IncidentStatus.CLOSED }
        return DashboardStats(
            averageRepairTimeMillis = if (repairTimes.isEmpty()) null else repairTimes.average().toLong(),
            totalIncidents = _incidents.size,
            activePercentage = if (_incidents.isEmpty()) 0.0 else activeCount * 100.0 / _incidents.size
        )
    }

    fun findGroupPrimary(categoryId: String, location: GeoLocation): Incident? =
        _incidents
            .filter {
                it.primaryIncidentId == null &&
                    it.categoryId == categoryId &&
                    it.status != IncidentStatus.CLOSED &&
                    LocationUtils.distanceMeters(it.location, location) <= GROUP_RADIUS_METERS
            }
            .minByOrNull { it.createdAt }

    fun groupSize(incident: Incident): Int {
        val primaryId = incident.primaryIncidentId ?: incident.id
        return 1 + _incidents.count { it.primaryIncidentId == primaryId }
    }

    fun upsert(incident: Incident) {
        val index = _incidents.indexOfFirst { it.id == incident.id }

        if (index >= 0) {
            _incidents[index] = incident
        } else {
            _incidents.add(incident)
        }
    }
}
