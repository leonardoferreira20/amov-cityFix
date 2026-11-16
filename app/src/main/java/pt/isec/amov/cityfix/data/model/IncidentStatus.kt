package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class IncidentStatus {
    OPEN,             // Aberto
    INFO_REQUESTED,   // Pedido de Informações
    IN_PROGRESS,      // Em Resolução
    RESOLVED,         // Resolvido
    CLOSED            // Encerrado
}