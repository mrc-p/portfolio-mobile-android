package data

import kotlinx.coroutines.flow.Flow
import network.ApiService
import java.io.IOException

// O Repository é a única fonte de dados para o ViewModel.
class ProjectRepository(
    private val apiService: ApiService,
    private val projectDao: ProjectDao
) {

    // Expõe o Flow do Room para o ViewModel (UI Reativa).\r\n
    val projects: Flow<List<Project>> = projectDao.getAllProjects()

    // NOVO: Expõe o Flow do Room para buscar um projeto específico por ID.
    fun getProjectById(projectId: Int): Flow<Project> {
        return projectDao.getProjectById(projectId)
    }

    // Lógica de sincronização de dados: busca na API e salva no Room.
    suspend fun refreshProjects() {
        try {
            // 1. Buscar dados da API
            val repos = apiService.getGitHubRepos()

            // 2. Mapear para entidades Room
            val projects = repos.map { it.toProjectEntity() }

            // 3. Salvar no Room (que notifica o Flow e atualiza a UI)
            projectDao.insertAll(projects)

        } catch (e: IOException) {
            // Em caso de erro de rede, apenas registra o erro
            println("Erro de sincronização de rede: ${e.message}")
        }
    }
}