package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User (
    val id : String = UUID.randomUUID().toString(),
    val name : String,
    val email : String,
    val phone : String,
    val nif : String,
    val role : String,
    val createdAt : String,
    var status : AccountStatus,
    var picturePath : String? = null,
    val location: String, //Georreferenciaçao???
    val address: String, //Alternativa morada????
) {
}