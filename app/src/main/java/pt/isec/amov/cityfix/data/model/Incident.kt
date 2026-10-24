package pt.isec.amov.cityfix.data.model

import java.util.UUID

data class Incident (
    val id : String = UUID.randomUUID().toString(),
    val authorId : String,
    val categoryId : String,
    val categoryName : String,
    val situation : Situation,
    var description: String,
    var justification: String,
    val visibility : Visibility,
    val location : String,
    val status: IncidentStatus,
    val previousStatus: IncidentStatus,
    val media: List<String>, //VER NA AULA DE MEDIA
    val primaryOccurrenceId : String,
    val createdAt : String,
    val workStartedAt: String,
    val closedAt : String,
    val resolvedAt : String,
    val reopen : Boolean
    ) {
}