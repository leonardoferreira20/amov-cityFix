package pt.isec.amov.cityfix.data.model

const val SYSTEM_USER_ID = "system"

fun Incident.workStarted(userId: String, now: Long): Incident = copy(
    status = IncidentStatus.IN_PROGRESS,
    workStartedAt = now,
    history = history + StatusChange(
        fromStatus = status,
        toStatus = IncidentStatus.IN_PROGRESS,
        timestamp = now,
        changedBy = userId
    )
)

fun Incident.resolved(userId: String, photoPath: String, note: String?, now: Long): Incident = copy(
    status = IncidentStatus.RESOLVED,
    resolvedAt = now,
    media = media + MediaAttachment(
        type = MediaType.IMAGE,
        purpose = MediaPurpose.RESOLUTION_PROOF,
        localUri = photoPath,
        uploadedBy = userId,
        createdAt = now
    ),
    history = history + StatusChange(
        fromStatus = status,
        toStatus = IncidentStatus.RESOLVED,
        timestamp = now,
        changedBy = userId,
        note = note
    )
)

fun Incident.closed(closedAt: Long): Incident = copy(
    status = IncidentStatus.CLOSED,
    closedAt = closedAt,
    history = history + StatusChange(
        fromStatus = status,
        toStatus = IncidentStatus.CLOSED,
        timestamp = closedAt,
        changedBy = SYSTEM_USER_ID
    )
)

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

fun Incident.withInformationRequest(request: InfoRequest): Incident {
    val updated = copy(infoRequests = infoRequests + request)
    if (request.requesterRole != UserRole.OPERATOR) return updated

    return updated.copy(
        status = IncidentStatus.INFO_REQUESTED,
        previousStatus = status,
        history = history + StatusChange(
            fromStatus = status,
            toStatus = IncidentStatus.INFO_REQUESTED,
            timestamp = request.createdAt,
            changedBy = request.requestedBy
        )
    )
}

fun Incident.withInformationReply(request: InfoRequest, reply: Message): Incident {
    val answeredRequest = request.copy(status = InfoRequestStatus.ANSWERED, answeredAt = reply.timestamp)
    val updated = copy(
        infoRequests = infoRequests.map { if (it.id == request.id) answeredRequest else it },
        messages = messages + reply
    )
    val restoredStatus = previousStatus ?: return updated
    if (request.requesterRole != UserRole.OPERATOR || status != IncidentStatus.INFO_REQUESTED) {
        return updated
    }

    return updated.copy(
        status = restoredStatus,
        previousStatus = null,
        history = history + StatusChange(
            fromStatus = status,
            toStatus = restoredStatus,
            timestamp = reply.timestamp,
            changedBy = reply.senderId
        )
    )
}
