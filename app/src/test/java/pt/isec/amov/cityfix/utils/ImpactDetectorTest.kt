package pt.isec.amov.cityfix.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImpactDetectorTest {
    private val upright = floatArrayOf(0f, 9.8f, 0f)
    private val flat = floatArrayOf(0f, 0f, 9.8f)

    private fun detectorAtRest(gravity: FloatArray) = ImpactDetector().apply {
        repeat(50) { isImpact(gravity) }
    }

    @Test
    fun phoneAtRestIsNotAnImpact() {
        val detector = ImpactDetector()
        repeat(50) { assertFalse(detector.isImpact(upright)) }
    }

    @Test
    fun suddenVerticalJoltIsAnImpact() {
        assertTrue(detectorAtRest(upright).isImpact(floatArrayOf(0f, 20f, 0f)))
    }

    @Test
    fun suddenVerticalDropIsAnImpact() {
        assertTrue(detectorAtRest(upright).isImpact(floatArrayOf(0f, 0f, 0f)))
    }

    @Test
    fun horizontalJoltIsNotAnImpact() {
        assertFalse(detectorAtRest(upright).isImpact(floatArrayOf(10f, 9.8f, 0f)))
    }

    @Test
    fun verticalJoltIsAnImpactWithThePhoneLyingFlat() {
        assertTrue(detectorAtRest(flat).isImpact(floatArrayOf(0f, 0f, 20f)))
    }

    @Test
    fun smallVibrationIsNotAnImpact() {
        assertFalse(detectorAtRest(upright).isImpact(floatArrayOf(0f, 12f, 0f)))
    }
}
