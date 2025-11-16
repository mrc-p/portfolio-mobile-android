package data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 1. Entidade Room (O Single Source of Truth)
@Entity(tableName = "projects")
data class Project(
    // ID do repositório no GitHub. Usado como chave primária.
    @PrimaryKey
    val id: Int,
    val name: String,
    // Note que description e language são nulos (String?).
    val description: String?,
    // Removi o '= null' daqui, pois são nulos e o DTO já garante a nulidade.
    val language: String?,
    val htmlUrl: String?
)

// 2. Data Class para a resposta da API do GitHub (DTO)
@Serializable
data class GitHubRepo(
    val id: Int,
    val name: String,
    val description: String?,
    val language: String?,
    @SerialName("html_url")
    val htmlUrl: String?
) {
    // Função de conversão da resposta da API para a Entidade Room
    fun toProjectEntity(): Project {
        return Project(
            id = this.id,
            name = this.name,
            description = this.description,
            language = this.language,
            htmlUrl = this.htmlUrl
        )
    }
}