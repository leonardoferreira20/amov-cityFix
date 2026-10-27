package pt.isec.amov.cityfix.data.model

import java.util.UUID

data class Incident (
    val id : String = UUID.randomUUID().toString(),
    val authorId : String,
    val categoryId : String,
    val categoryName : String,
    val situation : Situation,
    val description: String,
    val justification: String,
    val visibility : Visibility,
    val location : GeoPoint,
    val status: IncidentStatus,
    val previousStatus: IncidentStatus? = null,
    val media: List<String>, //VER NA AULA DE MEDIA
    val primaryOccurrenceId : String? = null,
    val createdAt : Long,
    val workStartedAt: Long? = null,
    val resolvedAt : Long? = null,
    val reopen : Boolean,
    val closedAt : Long? = null
    ) {

}