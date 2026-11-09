package pt.isec.amov.cityfix.data.model

class IncidentsList(initialIncidents: List<Incident> = emptyList()) {
    private val _incidents = initialIncidents.toMutableList()

    fun getAllSorted(): List<Incident> = _incidents.sortedByDescending { it.createdAt }

    fun findById(id: String): Incident? = _incidents.find { it.id == id }

    fun rawList(): List<Incident> = _incidents.toList()

    fun upsert(incident: Incident) {
        val index = _incidents.indexOfFirst { it.id == incident.id }

        if (index >= 0) {
            _incidents[index] = incident
        } else {
            _incidents.add(incident)
        }
    }
}
