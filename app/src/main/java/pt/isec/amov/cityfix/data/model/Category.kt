package pt.isec.amov.cityfix.data.model

import java.util.UUID

data class Category (
    val id : String = UUID.randomUUID().toString(),
    val name: String,
    var active: Boolean,
    val createdByName: String? = null,
    val createdAt: Long
    ) {

}