package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Project
import data.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

// Data class para encapsular o estado da UI de detalhes do projeto.
data class ProjectDetailUiState(
    val project: Project? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProjectDetailViewModel(
    projectId: Int,
    repository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailUiState(isLoading = true))
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    init {
        repository.getProjectById(projectId)
            .onEach { project ->
                _uiState.value = _uiState.value.copy(
                    project = project,
                    isLoading = false,
                    error = null
                )
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar projeto: ${e.message}"
                )
            }
            .launchIn(viewModelScope)
    }
}