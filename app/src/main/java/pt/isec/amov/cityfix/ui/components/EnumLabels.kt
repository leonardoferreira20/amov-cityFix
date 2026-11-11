package pt.isec.amov.cityfix.ui.components

import androidx.annotation.StringRes
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.Visibility

@StringRes
fun Situation.labelRes(): Int = when (this) {
    Situation.DAMAGE -> R.string.situation_damage
    Situation.MALFUNCTION -> R.string.situation_malfunction
    Situation.DESTRUCTION -> R.string.situation_destruction
    Situation.THEFT -> R.string.situation_theft
}

@StringRes
fun Visibility.labelRes(): Int = when (this) {
    Visibility.PUBLIC -> R.string.visibility_public
    Visibility.PRIVATE -> R.string.visibility_private
}

@StringRes
fun IncidentStatus.labelRes(): Int = when (this) {
    IncidentStatus.OPEN -> R.string.status_open
    IncidentStatus.INFO_REQUESTED -> R.string.status_info_requested
    IncidentStatus.IN_PROGRESS -> R.string.status_in_progress
    IncidentStatus.RESOLVED -> R.string.status_resolved
    IncidentStatus.CLOSED -> R.string.status_closed
}
