package viewmodel

import data.Project

// Data class para encapsular o estado da UI da lista de projetos.
// Este arquivo foi criado para que a classe de estado possa ser usada em múltiplos ViewModels.
data class ProjectUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)