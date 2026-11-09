package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isec.amov.cityfix.data.CategoryRepository

class EditCategoryViewModelFactory(
    private val repository: CategoryRepository,
    private val categoryId: String?,
    private val operatorId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        EditCategoryViewModel(repository, categoryId, operatorId) as T
}
