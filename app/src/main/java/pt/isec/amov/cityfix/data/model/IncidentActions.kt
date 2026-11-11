package pt.isec.amov.cityfix.data.model

fun Incident.reopened(userId: String, justification: String, now: Long): Incident = copy(
    status = IncidentStatus.OPEN,
    resolvedAt = null,
    reopenCount = reopenCount + 1,
    history = history + StatusChange(
        fromStatus = status,
        toStatus = IncidentStatus.OPEN,
        timestamp = now,
        changedBy = userId,
        note = justification
    )
)
