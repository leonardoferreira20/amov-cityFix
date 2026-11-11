package pt.isec.amov.cityfix.utils

import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.data.model.InfoRequest
import pt.isec.amov.cityfix.data.model.InfoRequestStatus
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.data.model.Visibility
import java.util.concurrent.TimeUnit

private val REOPEN_WINDOW_MILLIS = TimeUnit.DAYS.toMillis(5)

fun isOperator(user: User?): Boolean = user?.role == UserRole.OPERATOR

fun isCitizen(user: User?): Boolean = user?.role == UserRole.CITIZEN

fun isAuthor(user: User?, incident: Incident): Boolean = user != null && user.id == incident.authorId

fun canViewIncident(user: User?, incident: Incident): Boolean {
    if (isOperator(user) || isAuthor(user, incident)) return true
    return incident.visibility == Visibility.PUBLIC && incident.status != IncidentStatus.CLOSED
}

fun canSeeAuthorData(user: User?): Boolean = isOperator(user)

fun canSeeExactLocation(user: User?, incident: Incident): Boolean =
    isOperator(user) || isAuthor(user, incident)

fun canSeeHistory(user: User?, incident: Incident): Boolean =
    isOperator(user) || isAuthor(user, incident)

fun canCreateIncident(user: User?): Boolean = isCitizen(user)

fun canChangeStatus(user: User?): Boolean = isOperator(user)

fun canManageCategories(user: User?): Boolean = isOperator(user)

fun canValidateCitizens(user: User?): Boolean = isOperator(user)

fun canSeeInformationRequests(user: User?, incident: Incident): Boolean =
    isOperator(user) || isAuthor(user, incident)

fun canRequestInformation(user: User?, incident: Incident): Boolean {
    if (isOperator(user)) {
        return incident.status == IncidentStatus.OPEN || incident.status == IncidentStatus.IN_PROGRESS
    }
    return isCitizen(user) && isAuthor(user, incident) && incident.status != IncidentStatus.CLOSED
}

fun canReplyToInformationRequest(user: User?, incident: Incident, request: InfoRequest): Boolean {
    if (incident.status == IncidentStatus.CLOSED || request.status != InfoRequestStatus.OPEN) return false

    return when (request.requesterRole) {
        UserRole.OPERATOR -> isCitizen(user) && isAuthor(user, incident)
        UserRole.CITIZEN -> isOperator(user)
    }
}

fun reopenDeadline(incident: Incident): Long? = incident.resolvedAt?.plus(REOPEN_WINDOW_MILLIS)

fun canReopen(user: User?, incident: Incident, now: Long = System.currentTimeMillis()): Boolean {
    val deadline = reopenDeadline(incident) ?: return false
    return isAuthor(user, incident) &&
            incident.status == IncidentStatus.RESOLVED &&
            now <= deadline
}
