package pt.isec.amov.cityfix.utils

import pt.isec.amov.cityfix.data.model.GeoLocation
import java.util.Locale

fun GeoLocation.toDisplayText(): String =
    String.format(Locale.ROOT, "%.5f, %.5f", latitude, longitude)
