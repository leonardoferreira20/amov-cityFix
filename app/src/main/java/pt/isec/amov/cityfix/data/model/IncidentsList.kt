package pt.isec.amov.cityfix.data.model

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

    fun upsert(incident: Incident) {
        val index = _incidents.indexOfFirst { it.id == incident.id }

        if (index >= 0) {
            _incidents[index] = incident
        } else {
            _incidents.add(incident)
        }
    }
}
