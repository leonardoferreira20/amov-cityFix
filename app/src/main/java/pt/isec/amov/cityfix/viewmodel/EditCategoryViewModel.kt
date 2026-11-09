package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.data.CategoryRepository
import pt.isec.amov.cityfix.data.model.Category
import pt.isec.amov.cityfix.utils.Validators

class EditCategoryViewModel(
    private val repository: CategoryRepository,
    private val categoryId: String?,
    private val operatorId: String
) : ViewModel() {
    private val _editState = MutableStateFlow(EditCategoryUiState())
    val editState: StateFlow<EditCategoryUiState> = _editState.asStateFlow()

    init {
        if (categoryId != null) {
            viewModelScope.launch {
                repository.loadCategories().findById(categoryId)?.let { c ->
                    _editState.value = EditCategoryUiState(name = c.name)
                }
            }
        }
    }

    fun onNameChanged(newName: String) {
        _editState.update { it.copy(name = newName, nameError = Validators.nameError(newName)) }
    }

    fun saveCategory(onSuccess: () -> Unit) {
        val current = _editState.value
        if (!current.isValid) {
            onNameChanged(current.name)
            return
        }
        viewModelScope.launch {
            _editState.update { it.copy(isSaving = true) }

            val categoriesList = repository.loadCategories()
            val now = System.currentTimeMillis()
            val existing = categoryId?.let { categoriesList.findById(it) }
            val category = existing?.copy(name = current.name.trim(), updatedAt = now)
                ?: Category(name = current.name.trim(), createdBy = operatorId, createdAt = now)

            categoriesList.upsert(category)
            repository.saveCategories(categoriesList)
            _editState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
