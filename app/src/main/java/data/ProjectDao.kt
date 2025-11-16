package data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object) para o Room. Define as operações CRUD.
@Dao
interface ProjectDao {

    // Consulta para expor todos os projetos do banco de dados como um Kotlin Flow.
    @Suppress("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT * FROM projects ORDER BY name ASC")
    fun getAllProjects(): Flow<List<Project>>

    // NOVO: Consulta para expor um único projeto por ID como um Kotlin Flow.
    @Suppress("AndroidUnresolvedRoomSqlReference", "AndroidUnresolvedRoomSqlReference")
    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: Int): Flow<Project>

    // Insere ou substitui uma lista de projetos no banco de dados.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(projects: List<Project>)
}