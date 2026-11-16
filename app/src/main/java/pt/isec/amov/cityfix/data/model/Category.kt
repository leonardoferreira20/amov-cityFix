package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val active: Boolean = true,
    val createdBy: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long? = null
)
