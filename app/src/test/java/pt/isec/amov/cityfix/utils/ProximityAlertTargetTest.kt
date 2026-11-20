package pt.isec.amov.cityfix.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentStatus

class ProximityAlertTargetTest {
    @Test
    fun everyActiveStatusRaisesTheAlertAndClosedDoesNot() {
        val expected = mapOf(
            IncidentStatus.OPEN to true,
            IncidentStatus.INFO_REQUESTED to true,
            IncidentStatus.IN_PROGRESS to true,
            IncidentStatus.RESOLVED to true,
            IncidentStatus.CLOSED to false
        )
        IncidentStatus.entries.forEach { status ->
            assertEquals(status.name, expected.getValue(status), isProximityAlertTarget(Incident(status = status)))
        }
    }
}
