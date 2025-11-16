package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


// ViewModel que expõe o StateFlow e gerencia a sincronização (Room <-> API)
class ProjectListViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectUiState(isLoading = true))
    // StateFlow exposto para a UI (recursivo)
    val uiState: StateFlow<ProjectUiState> = _uiState.asStateFlow()

    init {
        // 1. Observa o Room (Source of Truth)
        repository.projects
            .onEach { projects ->
                _uiState.value = _uiState.value.copy(
                    projects = projects,
                    error = null
                )
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    // Se o Room falhar (muito raro), desliga o loader e mostra erro
                    isLoading = false,
                    error = "Erro ao carregar dados locais: ${e.message}"
                )
            }
            .launchIn(viewModelScope)

        // 2. Inicia a sincronização de rede
        refreshData()
    }

    // Função para iniciar a sincronização de dados
    fun refreshData() {
        viewModelScope.launch {
            // 1. Define o estado de carregamento, ativando o indicador
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Adiciona um delay para tornar o indicador de carregamento visível
            // (1 segundo de delay)
            delay(1000)

            try {
                // 2. Inicia a busca na API e o salvamento no Room
                repository.refreshProjects()
            } catch (_: Exception) {
                // Se houver qualquer erro não tratado pelo Repository, registra e mostra na UI.
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao sincronizar dados da rede. Verifique a conexão."
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
        }
    }
}