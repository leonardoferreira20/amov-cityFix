package pt.isec.amov.cityfix.data.model

fun Incident.reopened(userId: String, justification: String, photoPath: String, now: Long): Incident = copy(
    status = IncidentStatus.OPEN,
    resolvedAt = null,
    reopenCount = reopenCount + 1,
    media = media + MediaAttachment(
        type = MediaType.IMAGE,
        purpose = MediaPurpose.REOPEN,
        localUri = photoPath,
        uploadedBy = userId,
        createdAt = now
    ),
    history = history + StatusChange(
        fromStatus = status,
        toStatus = IncidentStatus.OPEN,
        timestamp = now,
        changedBy = userId,
        note = justification
    )
)
