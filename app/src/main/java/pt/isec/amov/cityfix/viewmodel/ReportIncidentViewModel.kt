package pt.isec.amov.cityfix.viewmodel

import androidx.annotation.StringRes
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
import pt.isec.amov.cityfix.data.model.MediaAttachment
import pt.isec.amov.cityfix.data.model.MediaPurpose
import pt.isec.amov.cityfix.data.model.MediaType
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.utils.FileUtils
import pt.isec.amov.cityfix.utils.isInsideCity
import pt.isec.amov.cityfix.utils.PAVEMENT_CATEGORY_ID
import pt.isec.amov.cityfix.utils.canCreateIncident

class ReportIncidentViewModel(
    private val incidentRepository: IncidentRepository,
    private val categoryRepository: CategoryRepository,
    private val currentUser: User?,
    private val impactLocation: GeoLocation? = null
) : ViewModel() {
    private val _reportState = MutableStateFlow(
        if (impactLocation == null) {
            ReportIncidentUiState()
        } else {
            ReportIncidentUiState(
                location = impactLocation,
                locationError = locationError(impactLocation),
                situation = Situation.DAMAGE
            )
        }
    )
    val reportState: StateFlow<ReportIncidentUiState> = _reportState.asStateFlow()

    init {
        viewModelScope.launch {
            val categories = categoryRepository.loadCategories().getActiveSorted()
            val pavementId = categories.find { it.id == PAVEMENT_CATEGORY_ID }?.id
            _reportState.update {
                it.copy(
                    categories = categories,
                    categoryId = if (impactLocation != null) pavementId else it.categoryId
                )
            }
        }
    }

    fun onLocationSelected(location: GeoLocation) {
        _reportState.update { it.copy(location = location, locationError = locationError(location)) }
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

    fun onVideoChanged(path: String?) {
        val previousPath = _reportState.value.videoPath
        if (previousPath != null && previousPath != path) FileUtils.deleteFromPath(previousPath)
        _reportState.update { it.copy(videoPath = path) }
    }

    fun saveIncident(onSuccess: () -> Unit) {
        val current = _reportState.value
        val author = currentUser?.takeIf(::canCreateIncident) ?: return
        val category = current.categories.find { it.id == current.categoryId }
        val situation = current.situation
        val photoPath = current.photoPath
        val location = current.location
        if (current.isSaving) return
        if (category == null || situation == null || photoPath == null || location == null ||
            !isInsideCity(location) || current.description.isBlank()
        ) {
            _reportState.update {
                it.copy(
                    categoryError = if (category == null) R.string.error_category_required else null,
                    situationError = if (situation == null) R.string.error_situation_required else null,
                    descriptionError = if (it.description.isBlank()) R.string.error_description_required else null,
                    photoError = if (photoPath == null) R.string.error_photo_required else null,
                    locationError = locationError(location)
                )
            }
            return
        }

        _reportState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val primary = incidentRepository.loadIncidents().findGroupPrimary(category.id, location)
            incidentRepository.saveIncident(
                Incident(
                    authorId = author.id,
                    categoryId = category.id,
                    categoryName = category.name,
                    situation = situation,
                    description = current.description.trim(),
                    visibility = current.visibility,
                    location = location,
                    media = listOfNotNull(
                        MediaAttachment(
                            type = MediaType.IMAGE,
                            purpose = MediaPurpose.REPORT,
                            localUri = photoPath,
                            uploadedBy = author.id,
                            createdAt = now
                        ),
                        current.videoPath?.let { videoPath ->
                            MediaAttachment(
                                type = MediaType.VIDEO,
                                purpose = MediaPurpose.REPORT,
                                localUri = videoPath,
                                uploadedBy = author.id,
                                createdAt = now
                            )
                        }
                    ),
                    primaryIncidentId = primary?.id,
                    createdAt = now
                )
            )
            _reportState.update { it.copy(isSaving = false, isSaved = true) }
            onSuccess()
        }
    }

    override fun onCleared() {
        val state = _reportState.value
        if (!state.isSaved) {
            FileUtils.deleteFromPath(state.photoPath)
            FileUtils.deleteFromPath(state.videoPath)
        }
    }
}

@StringRes
private fun locationError(location: GeoLocation?): Int? = when {
    location == null -> R.string.error_location_required
    !isInsideCity(location) -> R.string.error_location_outside_city
    else -> null
}
