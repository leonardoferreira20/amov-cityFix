package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.data.CategoryRepository
import pt.isec.amov.cityfix.data.model.CategoriesList
import pt.isec.amov.cityfix.data.model.Category

class CategoriesViewModel(private val repository: CategoryRepository) : ViewModel() {
    private val _categoriesList = MutableStateFlow<List<Category>>(emptyList())
    val categoriesList: StateFlow<List<Category>> = _categoriesList.asStateFlow()
    private var currentCategoriesList = CategoriesList()

    init {
        refreshCategories()
    }

    fun refreshCategories() {
        viewModelScope.launch {
            currentCategoriesList = repository.loadCategories()
            _categoriesList.value = currentCategoriesList.getAllSorted()
        }
    }

    fun setCategoryActive(categoryId: String, active: Boolean) {
        val category = currentCategoriesList.findById(categoryId) ?: return
        viewModelScope.launch {
            currentCategoriesList.upsert(
                category.copy(active = active, updatedAt = System.currentTimeMillis())
            )
            repository.saveCategories(currentCategoriesList)
            _categoriesList.value = currentCategoriesList.getAllSorted()
        }
    }
}
