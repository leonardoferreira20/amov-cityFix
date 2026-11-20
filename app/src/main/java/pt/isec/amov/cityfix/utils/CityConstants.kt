package pt.isec.amov.cityfix.utils

import pt.isec.amov.cityfix.data.model.GeoLocation

val CITY_CENTER = GeoLocation(40.2085, -8.4196)

const val ANONYMOUS_RADIUS_METERS = 10_000f

const val GROUP_RADIUS_METERS = 10f

const val PROXIMITY_RADIUS_METERS = 20f

const val IMPACT_ACCELERATION_THRESHOLD = 6f

const val IMPACT_MIN_SPEED_KMH = 10f

const val IMPACT_SUGGESTION_INTERVAL_MILLIS = 60_000L

const val PAVEMENT_CATEGORY_ID = "cat-roads"

const val MAX_VIDEO_SECONDS = 30

const val MAX_VIDEO_BYTES = 50L * 1024 * 1024
