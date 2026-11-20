package pt.isec.amov.cityfix.utils

import kotlin.math.abs
import kotlin.math.sqrt

class ImpactDetector(private val threshold: Float = IMPACT_ACCELERATION_THRESHOLD) {
    private val gravity = FloatArray(3)
    private var hasGravity = false

    fun isImpact(values: FloatArray): Boolean {
        if (!hasGravity) {
            values.copyInto(gravity, endIndex = 3)
            hasGravity = true
            return false
        }
        for (i in 0..2) gravity[i] = ALPHA * gravity[i] + (1 - ALPHA) * values[i]

        val gravityNorm = sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2])
        if (gravityNorm == 0f) return false
        val vertical = ((values[0] - gravity[0]) * gravity[0] +
            (values[1] - gravity[1]) * gravity[1] +
            (values[2] - gravity[2]) * gravity[2]) / gravityNorm
        return abs(vertical) >= threshold
    }

    private companion object {
        const val ALPHA = 0.8f
    }
}
