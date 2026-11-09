package pt.isec.amov.cityfix.data.model

data class DashboardStats(
    val averageRepairTimeMillis: Long?,
    val totalIncidents: Int,
    val activePercentage: Double
)
