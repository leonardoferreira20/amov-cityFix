package pt.isec.amov.cityfix.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import pt.isec.amov.cityfix.data.model.CategoriesList
import pt.isec.amov.cityfix.data.model.Category
import java.io.File

class CategoryRepository(private val context: Context) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val databaseFile by lazy { File(context.filesDir, "categories_database.json") }
    private var cachedList: CategoriesList? = null

    private fun getSeedData(): List<Category> {
        val now = System.currentTimeMillis()
        return listOf(
            Category(id = "cat-roads", name = "Vias Públicas", createdBy = "operator-1", createdAt = now),
            Category(id = "cat-water", name = "Água", createdBy = "operator-1", createdAt = now),
            Category(id = "cat-lighting", name = "Iluminação", createdBy = "operator-1", createdAt = now),
            Category(id = "cat-sanitation", name = "Saneamento", createdBy = "operator-1", createdAt = now),
            Category(
                id = "cat-green",
                name = "Espaços Verdes",
                active = false,
                createdBy = "operator-1",
                createdAt = now
            )
        )
    }

    suspend fun loadCategories(): CategoriesList = withContext(Dispatchers.IO) {
        cachedList?.let { return@withContext it }
        val list = if (!databaseFile.exists()) {
            CategoriesList(getSeedData())
        } else {
            try {
                CategoriesList(json.decodeFromString<List<Category>>(databaseFile.readText()))
            } catch (_: Exception) {
                CategoriesList(getSeedData())
            }
        }
        cachedList = list
        list
    }

    suspend fun saveCategories(list: CategoriesList) = withContext(Dispatchers.IO) {
        cachedList = list
        databaseFile.writeText(json.encodeToString(list.rawList()))
    }
}
