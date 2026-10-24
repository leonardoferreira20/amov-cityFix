package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    CITIZEN,
    OPERATOR
}