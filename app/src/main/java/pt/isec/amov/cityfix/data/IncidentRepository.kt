package pt.isec.amov.cityfix.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.data.model.IncidentsList
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.StatusChange
import pt.isec.amov.cityfix.data.model.Visibility
import java.io.File

private const val DAY_MILLIS = 24 * 60 * 60 * 1000L

class IncidentRepository(private val context: Context) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val databaseFile by lazy { File(context.filesDir, "incidents_database.json") }
    private var cachedList: IncidentsList? = null

    private fun getSeedData(): List<Incident> {
        val now = System.currentTimeMillis()
        return listOf(
            Incident(
                authorId = "citizen-1",
                categoryId = "cat-roads",
                categoryName = "Vias Públicas",
                situation = Situation.DAMAGE,
                description = "Buraco grande na faixa da direita.",
                visibility = Visibility.PUBLIC,
                location = GeoLocation(40.2086, -8.4207),
                createdAt = now - 2 * DAY_MILLIS
            ),
            Incident(
                authorId = "citizen-2",
                categoryId = "cat-lighting",
                categoryName = "Iluminação",
                situation = Situation.MALFUNCTION,
                description = "Candeeiro apagado há uma semana.",
                visibility = Visibility.PUBLIC,
                location = GeoLocation(40.2101, -8.4189),
                status = IncidentStatus.IN_PROGRESS,
                createdAt = now - 6 * DAY_MILLIS,
                workStartedAt = now - DAY_MILLIS
            ),
            Incident(
                authorId = "citizen-1",
                categoryId = "cat-water",
                categoryName = "Água",
                situation = Situation.DESTRUCTION,
                description = "Rotura na conduta junto ao passeio.",
                visibility = Visibility.PRIVATE,
                location = GeoLocation(40.2072, -8.4231),
                status = IncidentStatus.RESOLVED,
                createdAt = now - 10 * DAY_MILLIS,
                workStartedAt = now - 8 * DAY_MILLIS,
                resolvedAt = now - 2 * DAY_MILLIS
            ),
            Incident(
                authorId = "citizen-2",
                categoryId = "cat-roads",
                categoryName = "Vias Públicas",
                situation = Situation.THEFT,
                description = "Sinal de stop roubado no cruzamento.",
                visibility = Visibility.PUBLIC,
                location = GeoLocation(40.2115, -8.4250),
                status = IncidentStatus.CLOSED,
                createdAt = now - 20 * DAY_MILLIS,
                workStartedAt = now - 18 * DAY_MILLIS,
                resolvedAt = now - 15 * DAY_MILLIS,
                closedAt = now - 10 * DAY_MILLIS
            ),
            Incident(
                authorId = "citizen-1",
                categoryId = "cat-lighting",
                categoryName = "Iluminação",
                situation = Situation.MALFUNCTION,
                description = "Semáforo avariado no cruzamento da Avenida Sá da Bandeira.",
                visibility = Visibility.PUBLIC,
                location = GeoLocation(40.2095, -8.4215),
                status = IncidentStatus.RESOLVED,
                history = listOf(
                    StatusChange(IncidentStatus.OPEN, IncidentStatus.IN_PROGRESS, now - 4 * DAY_MILLIS, "operator-1"),
                    StatusChange(IncidentStatus.IN_PROGRESS, IncidentStatus.RESOLVED, now - DAY_MILLIS, "operator-1")
                ),
                createdAt = now - 7 * DAY_MILLIS,
                workStartedAt = now - 4 * DAY_MILLIS,
                resolvedAt = now - DAY_MILLIS
            ),
            Incident(
                authorId = "citizen-1",
                categoryId = "cat-sanitation",
                categoryName = "Saneamento",
                situation = Situation.DAMAGE,
                description = "Tampa de esgoto partida em frente à escola.",
                visibility = Visibility.PUBLIC,
                location = GeoLocation(40.2079, -8.4202),
                status = IncidentStatus.CLOSED,
                history = listOf(
                    StatusChange(IncidentStatus.OPEN, IncidentStatus.IN_PROGRESS, now - 25 * DAY_MILLIS, "operator-1"),
                    StatusChange(IncidentStatus.IN_PROGRESS, IncidentStatus.RESOLVED, now - 22 * DAY_MILLIS, "operator-1"),
                    StatusChange(IncidentStatus.RESOLVED, IncidentStatus.CLOSED, now - 17 * DAY_MILLIS, "operator-1")
                ),
                createdAt = now - 30 * DAY_MILLIS,
                workStartedAt = now - 25 * DAY_MILLIS,
                resolvedAt = now - 22 * DAY_MILLIS,
                closedAt = now - 17 * DAY_MILLIS
            )
        )
    }

    fun getIncidentById(id: String): Incident? = cachedList?.findById(id)

    suspend fun loadIncidents(): IncidentsList = withContext(Dispatchers.IO) {
        cachedList?.let { return@withContext it }
        val list = if (!databaseFile.exists()) {
            IncidentsList(getSeedData())
        } else {
            try {
                IncidentsList(json.decodeFromString<List<Incident>>(databaseFile.readText()))
            } catch (_: Exception) {
                IncidentsList(getSeedData())
            }
        }
        cachedList = list
        list
    }

    suspend fun saveIncidents(list: IncidentsList) = withContext(Dispatchers.IO) {
        cachedList = list
        databaseFile.writeText(json.encodeToString(list.rawList()))
    }
}
