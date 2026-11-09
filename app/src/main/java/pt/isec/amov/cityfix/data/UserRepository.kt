package pt.isec.amov.cityfix.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.data.model.UsersList
import java.io.File

class UserRepository(private val context: Context) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val databaseFile by lazy { File(context.filesDir, "users_database.json") }
    private var cachedList: UsersList? = null

    private fun getSeedData(): List<User> {
        val now = System.currentTimeMillis()
        return listOf(
            User(
                id = "operator-1",
                name = "Ana Martins",
                email = "ana.martins@aac-coimbra.pt",
                phone = "239000001",
                nif = "123456789",
                role = UserRole.OPERATOR,
                status = AccountStatus.ACTIVE,
                createdAt = now
            ),
            User(
                id = "citizen-1",
                name = "Bruno Costa",
                email = "bruno.costa@example.com",
                phone = "912000001",
                nif = "234567890",
                role = UserRole.CITIZEN,
                status = AccountStatus.ACTIVE,
                createdAt = now,
                location = GeoLocation(40.2086, -8.4207),
                impactDetectionEnabled = true
            ),
            User(
                id = "citizen-2",
                name = "Carla Sousa",
                email = "carla.sousa@example.com",
                phone = "913000002",
                nif = "345678901",
                role = UserRole.CITIZEN,
                status = AccountStatus.PENDING,
                createdAt = now,
                location = GeoLocation(40.2101, -8.4189)
            )
        )
    }

    suspend fun loadUsers(): UsersList = withContext(Dispatchers.IO) {
        cachedList?.let { return@withContext it }
        val list = if (!databaseFile.exists()) {
            UsersList(getSeedData())
        } else {
            try {
                UsersList(json.decodeFromString<List<User>>(databaseFile.readText()))
            } catch (_: Exception) {
                UsersList(getSeedData())
            }
        }
        cachedList = list
        list
    }

    suspend fun saveUsers(list: UsersList) = withContext(Dispatchers.IO) {
        cachedList = list
        databaseFile.writeText(json.encodeToString(list.rawList()))
    }
}
