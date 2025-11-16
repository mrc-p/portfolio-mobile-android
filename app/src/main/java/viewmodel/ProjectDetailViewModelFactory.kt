package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import data.ProjectRepository

class ProjectDetailViewModelFactory(
    private val projectId: Int,
    private val repository: ProjectRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDetailViewModel::class.java)) {
            // Instancia o ProjectDetailViewModel passando o projectId e o repository
            return ProjectDetailViewModel(projectId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}