package pt.isec.amov.cityfix.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import pt.isec.amov.cityfix.data.model.CategoriesList
import pt.isec.amov.cityfix.data.model.Category

class CategoryRepository {
    private val collection = Firebase.firestore.collection("categories")

    private fun getSeedData(operatorId: String): List<Category> {
        val now = System.currentTimeMillis()
        return listOf(
            Category(id = "cat-roads", name = "Vias Públicas", createdBy = operatorId, createdAt = now),
            Category(id = "cat-water", name = "Água", createdBy = operatorId, createdAt = now),
            Category(id = "cat-lighting", name = "Iluminação", createdBy = operatorId, createdAt = now),
            Category(id = "cat-sanitation", name = "Saneamento", createdBy = operatorId, createdAt = now),
            Category(id = "cat-green", name = "Espaços Verdes", active = false, createdBy = operatorId, createdAt = now)
        )
    }

    suspend fun loadCategories(): CategoriesList = try {
        CategoriesList(collection.get().await().toObjects(Category::class.java))
    } catch (_: Exception) {
        CategoriesList()
    }

    fun saveCategory(category: Category) {
        collection.document(category.id).set(category)
    }

    suspend fun seedIfEmpty(operatorId: String) {
        val isEmpty = try {
            collection.limit(1).get().await().isEmpty
        } catch (_: Exception) {
            false
        }
        if (isEmpty) getSeedData(operatorId).forEach { saveCategory(it) }
    }
}
