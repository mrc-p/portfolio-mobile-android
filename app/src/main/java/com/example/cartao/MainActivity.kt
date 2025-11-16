package com.example.cartao

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartao.ui.theme.CartaoTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room

// Importações de Rede e Persistência
import data.AppDatabase
import data.Project
import data.ProjectRepository
import network.ApiService
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import viewmodel.ProjectListViewModel
import viewmodel.ProjectListViewModelFactory
// Importações para a Tela de Detalhes
import viewmodel.ProjectDetailViewModel
import viewmodel.ProjectDetailViewModelFactory
import viewmodel.ProjectDetailUiState
import android.content.Intent
import android.net.Uri

// Importação de logging para OkHttp para ver chamadas de API
import okhttp3.logging.HttpLoggingInterceptor


class MainActivity : ComponentActivity() {

    // Instâncias da arquitetura
    private lateinit var database: AppDatabase
    private lateinit var apiService: ApiService
    private lateinit var repository: ProjectRepository
    private lateinit var viewModelFactory: ProjectListViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- 1. Configuração do Room (Persistência Local) ---
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "project_db"
        )
            .allowMainThreadQueries() // Permitido APENAS para o Build da DB. Operações DAO são suspensas.
            .build()

        // --- 2. Configuração do Retrofit (Rede) com KTS ---
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        // Adiciona um interceptor de log para ver as chamadas de rede no Logcat
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Define o nível de detalhe do log
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()


        val retrofit = Retrofit.Builder()
            // URL Base do GitHub API
            .baseUrl("https://api.github.com/")
            // Cliente OkHttp com o interceptor de log
            .client(httpClient)
            // Conversor Kotlinx Serialization
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // --- 3. Configuração do Repository e ViewModel Factory ---
        repository = ProjectRepository(apiService, database.projectDao())
        // Injeção de dependência básica via Factory
        viewModelFactory = ProjectListViewModelFactory(repository)
        // ---------------------------------------------------

        setContent {
            CartaoTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "profile"
                ) {
                    composable("profile") {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CartaoDeVisitas(navController)
                        }
                    }
                    composable("project_list") {
                        // USANDO O VIEWMODEL COM O FACTORY
                        ProjectListScreen(
                            navController,
                            viewModel = viewModel(factory = viewModelFactory)
                        )
                    }
                    composable("project_detail/{projectId}") { backStackEntry ->
                        val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()

                        // Instancia o Factory de Detalhes APENAS se o ID for válido
                        if (projectId != null) {
                            // 🚨 CORREÇÃO: Argumentos corrigidos para (projectId, repository)
                            // A classe ProjectDetailViewModelFactory espera projectId: Int, seguido por repository: ProjectRepository.
                            val detailViewModelFactory = ProjectDetailViewModelFactory(projectId, repository)
                            ProjectDetailScreen(
                                navController,
                                projectId,
                                viewModel = viewModel(factory = detailViewModelFactory),
                                // Passa o contexto para abrir o link
                                context = this@MainActivity
                            )
                        } else {
                            // Caso o ID seja nulo ou inválido, passa null para o ViewModel
                            ProjectDetailScreen(navController, null, null, this@MainActivity)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------
// Componentes Compose
// -------------------------------------------------------------------

@Composable
fun CartaoDeVisitas(navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Sombra
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Parte Superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(red = 12, green = 49, blue = 82, alpha = 255))
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        // O drawable `R.drawable.avatar` deve existir no projeto.
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Marcos Paulo Pires Silva",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Tecnólogo em Sistemas para Internet",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            // Parte Inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(red = 238, green = 239, blue = 241, alpha = 255))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                ContatoItem(R.drawable.baseline_phone_24, "(84) 996278664")
                ContatoItem(R.drawable.ic_whatsapp, "(84) 996278664")
                ContatoItem(R.drawable.outline_alternate_email_24, "marcos.paulo.spires@gmail.com")
                ContatoItem(R.drawable.ic_discord, "mrk_s")

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ContatoItemHorizontal(R.drawable.ic_github, "GitHub/mrc-p")
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("project_list") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(red = 37, green = 86, blue = 128, alpha = 255),
                        contentColor = Color.White,
                    )
                ) {
                    Text(text = "Meus Projetos",
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ContatoItem(iconId: Int, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = texto, fontSize = 16.sp)
    }
}

@Composable
fun ContatoItemHorizontal(iconId: Int, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, fontSize = 16.sp)
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getCustomTopAppBarColors(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = Color.White,
        titleContentColor = Color(red = 12, green = 49, blue = 82),
        navigationIconContentColor = Color(red = 12, green = 49, blue = 82)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    navController: NavHostController,
    // Recebe o ViewModel injetado
    viewModel: ProjectListViewModel
) {
    // Observa o StateFlow para obter o estado reativo da UI
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Meus Projetos") },
                colors = getCustomTopAppBarColors(),
                navigationIcon = {
                    BackButton(onClick = { navController.popBackStack() })
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {

            // O indicador de carregamento aparece se a flag isLoading for TRUE
            if (uiState.isLoading) {
                // ALTERAÇÃO AQUI: Aumentando o tamanho e a espessura do indicador
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp), // Aumenta o tamanho total do indicador
                    color = Color(red = 37, green = 86, blue = 128), // Cor de destaque do tema
                    strokeWidth = 6.dp // Aumenta a espessura da linha
                )
            }
            // Exibe erro se houver
            else if (uiState.error != null) {
                Text(
                    text = "Erro: ${uiState.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            // Exibe a lista (se houver projetos) ou a mensagem de "Nenhum Projeto" (se não houver projetos)
            else if (uiState.projects.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.projects) { project ->
                        ProjectCard(project = project) {
                            // Navega para a tela de detalhes com o ID do projeto
                            navController.navigate("project_detail/${project.id}")
                        }
                    }
                }
            } else {
                Text(
                    text = "Nenhum projeto encontrado.\nVerifique sua conexão e tente novamente .",
                    color = Color(red = 12, green = 49, blue = 82),
                    fontSize = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(red = 238, green = 239, blue = 241, alpha = 255),
            contentColor = Color(red = 12, green = 49, blue = 82)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = project.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            // Tratamento para campo nulo
            Text(
                text = project.language?.let { "Linguagem: $it" } ?: "Linguagem: N/A",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = project.description ?: "Nenhuma descrição disponível.",
                fontSize = 14.sp,
                maxLines = 2
            )
        }
    }
}


@SuppressLint("UseKtx")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    navController: NavHostController,
    projectId: Int?,
    // Recebe o ViewModel injetado
    viewModel: ProjectDetailViewModel? = null,
    context: ComponentActivity
) {
    // Define o estado padrão para o caso de ViewModel ser nulo (ID inválido ou erro)
    val defaultUiState = ProjectDetailUiState(
        isLoading = projectId != null, // Só mostra carregando se um ID válido foi tentado
        error = if (projectId == null) "ID do projeto inválido." else null
    )

    // Observa o StateFlow se o ViewModel existir, caso contrário, usa um estado estático (defaultUiState)
    val uiStateState: State<ProjectDetailUiState> =
        viewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(defaultUiState) }

    // Obtém o valor do estado observado/estático
    val uiState = uiStateState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalhes do Projeto") },
                colors = getCustomTopAppBarColors(),
                navigationIcon = {
                    BackButton(onClick = { navController.popBackStack() })
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. Loading
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(red = 37, green = 86, blue = 128)
                )
            }
            // 2. Error
            else if (uiState.error != null) {
                Text(
                    text = "Erro: ${uiState.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // 3. Content
            else if (uiState.project != null) {
                val project = uiState.project
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = project.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(red = 12, green = 49, blue = 82)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = project.language?.let { "Linguagem Principal: $it" } ?: "Linguagem: N/A",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Descrição:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(red = 37, green = 86, blue = 128)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = project.description ?: "Nenhuma descrição detalhada disponível.",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botão para abrir o link do GitHub
                    project.htmlUrl?.let { url ->
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(red = 37, green = 86, blue = 128, alpha = 255),
                                contentColor = Color.White,
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_github),
                                    contentDescription = "GitHub Icon",
                                    modifier = Modifier.size(24.dp),
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ver no GitHub", fontSize = 17.sp)
                            }
                        }
                    }
                }
            }
            // 4. Nenhum Projeto Encontrado (Erro do Room)
            else {
                Text(
                    text = "Projeto ID $projectId não encontrado no banco de dados local.",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}