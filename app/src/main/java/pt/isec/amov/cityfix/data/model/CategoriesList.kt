package pt.isec.amov.cityfix.data.model

import java.text.Collator
import java.util.Locale

class CategoriesList(initialCategories: List<Category> = emptyList()) {
    private val _categories = initialCategories.toMutableList()

    fun getAllSorted(): List<Category> {
        val collator = Collator.getInstance(Locale.getDefault()).apply {
            strength = Collator.PRIMARY
        }

        return _categories.sortedWith { c1, c2 -> collator.compare(c1.name, c2.name) }
    }

    fun getActiveSorted(): List<Category> = getAllSorted().filter { it.active }

    fun findById(id: String): Category? = _categories.find { it.id == id }

    fun rawList(): List<Category> = _categories.toList()

    fun upsert(category: Category) {
        val index = _categories.indexOfFirst { it.id == category.id }

        if (index >= 0) {
            _categories[index] = category
        } else {
            _categories.add(category)
        }
    }
}
