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
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentsList
import pt.isec.amov.cityfix.data.model.MediaAttachment
import pt.isec.amov.cityfix.data.model.MediaPurpose
import pt.isec.amov.cityfix.data.model.MediaType
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.utils.FileUtils
import java.io.IOException

class ReportIncidentViewModel(
    private val incidentRepository: IncidentRepository,
    private val categoryRepository: CategoryRepository,
    private val currentUser: User?
) : ViewModel() {
    private val _reportState = MutableStateFlow(ReportIncidentUiState())
    val reportState: StateFlow<ReportIncidentUiState> = _reportState.asStateFlow()

    private var deviceLocation: GeoLocation? = null

    init {
        viewModelScope.launch {
            val categories = categoryRepository.loadCategories().getActiveSorted()
            _reportState.update { it.copy(categories = categories) }
        }
    }

    fun onDeviceLocationChanged(location: GeoLocation?) {
        deviceLocation = location
        if (location != null && _reportState.value.photoPath != null && _reportState.value.location == null) {
            _reportState.update { it.copy(location = location, locationError = null) }
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
        _reportState.update {
            it.copy(photoPath = path, photoError = null, location = deviceLocation, locationError = null)
        }
    }

    fun saveIncident(onSuccess: () -> Unit) {
        val current = _reportState.value
        val author = currentUser ?: return
        val category = current.categories.find { it.id == current.categoryId }
        val situation = current.situation
        val photoPath = current.photoPath
        val location = current.location
        if (current.isSaving) return
        if (category == null || situation == null || photoPath == null || location == null ||
            current.description.isBlank()
        ) {
            _reportState.update {
                it.copy(
                    categoryError = if (category == null) R.string.error_category_required else null,
                    situationError = if (situation == null) R.string.error_situation_required else null,
                    descriptionError = if (it.description.isBlank()) R.string.error_description_required else null,
                    photoError = if (photoPath == null) R.string.error_photo_required else null,
                    locationError = if (photoPath != null && location == null) R.string.error_location_required else null
                )
            }
            return
        }

        _reportState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val incidents = IncidentsList(incidentRepository.loadIncidents().rawList())
                val primary = incidents.findGroupPrimary(category.id, location)
                incidents.upsert(
                    Incident(
                        authorId = author.id,
                        categoryId = category.id,
                        categoryName = category.name,
                        situation = situation,
                        description = current.description.trim(),
                        visibility = current.visibility,
                        location = location,
                        media = listOf(
                            MediaAttachment(
                                type = MediaType.IMAGE,
                                purpose = MediaPurpose.REPORT,
                                localUri = photoPath,
                                uploadedBy = author.id,
                                createdAt = now
                            )
                        ),
                        primaryIncidentId = primary?.id,
                        createdAt = now
                    )
                )
                incidentRepository.saveIncidents(incidents)
                _reportState.update { it.copy(isSaving = false, isSaved = true) }
                onSuccess()
            } catch (_: IOException) {
                _reportState.update { it.copy(isSaving = false, saveError = R.string.error_save_failed) }
            }
        }
    }

    override fun onCleared() {
        val state = _reportState.value
        if (!state.isSaved) FileUtils.deleteFromPath(state.photoPath)
    }
}
