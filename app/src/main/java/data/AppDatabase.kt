package data

import androidx.room.Database
import androidx.room.RoomDatabase

// Declara as entidades e a versão do banco de dados.
@Database(entities = [Project::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // O DAO para acessar os dados.
    abstract fun projectDao(): ProjectDao

}