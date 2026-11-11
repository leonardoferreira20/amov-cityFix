package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.CategoryRepository
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.MediaAttachment
import pt.isec.amov.cityfix.data.model.MediaPurpose
import pt.isec.amov.cityfix.data.model.MediaType
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.utils.CITY_CENTER
import pt.isec.amov.cityfix.utils.FileUtils

class ReportIncidentViewModel(
    private val incidentRepository: IncidentRepository,
    private val categoryRepository: CategoryRepository,
    private val currentUser: User?
) : ViewModel() {
    private val _reportState = MutableStateFlow(ReportIncidentUiState())
    val reportState: StateFlow<ReportIncidentUiState> = _reportState.asStateFlow()

    init {
        viewModelScope.launch {
            val categories = categoryRepository.loadCategories().getActiveSorted()
            _reportState.update { it.copy(categories = categories) }
        }
    }

    fun onCategorySelected(categoryId: String) {
        _reportState.update { it.copy(categoryId = categoryId, categoryError = null) }
    }

    fun onSituationSelected(situation: Situation) {
        _reportState.update { it.copy(situation = situation, situationError = null) }
    }

    fun onDescriptionChanged(description: String) {
        _reportState.update {
            it.copy(
                description = description,
                descriptionError = if (description.isBlank()) R.string.error_description_required else null
            )
        }
    }

    fun onVisibilityChanged(visibility: Visibility) {
        _reportState.update { it.copy(visibility = visibility) }
    }

    fun onPhotoChanged(path: String?) {
        val previousPath = _reportState.value.photoPath
        if (previousPath != null && previousPath != path) FileUtils.deleteFromPath(previousPath)
        _reportState.update { it.copy(photoPath = path, photoError = null) }
    }

    fun saveIncident(onSuccess: () -> Unit) {
        val current = _reportState.value
        val author = currentUser ?: return
        val category = current.categories.find { it.id == current.categoryId }
        val situation = current.situation
        val photoPath = current.photoPath
        if (current.isSaving) return
        if (category == null || situation == null || photoPath == null || current.description.isBlank()) {
            _reportState.update {
                it.copy(
                    categoryError = if (category == null) R.string.error_category_required else null,
                    situationError = if (situation == null) R.string.error_situation_required else null,
                    descriptionError = if (it.description.isBlank()) R.string.error_description_required else null,
                    photoError = if (photoPath == null) R.string.error_photo_required else null
                )
            }
            return
        }

        viewModelScope.launch {
            _reportState.update { it.copy(isSaving = true) }
            val now = System.currentTimeMillis()
            val incident = Incident(
                authorId = author.id,
                categoryId = category.id,
                categoryName = category.name,
                situation = situation,
                description = current.description.trim(),
                visibility = current.visibility,
                location = author.location ?: CITY_CENTER,
                media = listOf(
                    MediaAttachment(
                        type = MediaType.IMAGE,
                        purpose = MediaPurpose.REPORT,
                        localUri = photoPath,
                        uploadedBy = author.id,
                        createdAt = now
                    )
                ),
                createdAt = now
            )
            val incidents = incidentRepository.loadIncidents()
            incidents.upsert(incident)
            incidentRepository.saveIncidents(incidents)
            _reportState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
