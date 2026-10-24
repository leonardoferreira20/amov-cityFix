package pt.isec.amov.cityfix.data.model

import java.util.UUID

data class Category (
    val id : String = UUID.randomUUID().toString(),
    val name: String,
    val createdBy: User? =null,
    val createdAt: String
    ) {

}