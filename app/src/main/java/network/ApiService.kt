package network

import data.GitHubRepo
import retrofit2.http.GET

// Interface para as chamadas de API.
interface ApiService {

    // Retorna uma lista de objetos GitHubRepo do pacote data.
    @GET("users/mrc-p/repos")
    suspend fun getGitHubRepos(): List<GitHubRepo>
}