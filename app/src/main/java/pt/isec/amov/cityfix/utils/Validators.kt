package pt.isec.amov.cityfix.utils

import androidx.annotation.StringRes
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.UserRole

object Validators {
    private const val OPERATOR_EMAIL_DOMAIN = "@aac-coimbra.pt"

    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    private val PHONE_REGEX = "^[29][0-9]{8}$".toRegex()
    private val NIF_REGEX = "^[0-9]{9}$".toRegex()

    @StringRes
    fun nameError(name: String): Int? =
        if (name.isBlank()) R.string.error_name_required else null

    @StringRes
    fun emailError(email: String, role: UserRole): Int? {
        val trimmed = email.trim()
        return when {
            !EMAIL_REGEX.matches(trimmed) -> R.string.error_email_invalid
            role == UserRole.OPERATOR && !trimmed.endsWith(OPERATOR_EMAIL_DOMAIN, ignoreCase = true) ->
                R.string.error_email_operator_domain
            else -> null
        }
    }

    @StringRes
    fun phoneError(phone: String): Int? =
        if (PHONE_REGEX.matches(phone.trim())) null else R.string.error_phone_invalid

    @StringRes
    fun nifError(nif: String): Int? =
        if (NIF_REGEX.matches(nif.trim())) null else R.string.error_nif_invalid
}
