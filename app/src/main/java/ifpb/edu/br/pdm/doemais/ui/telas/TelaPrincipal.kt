package ifpb.edu.br.pdm.doemais.ui.telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ifpb.edu.br.pdm.doemais.model.BancoDAO
import ifpb.edu.br.pdm.doemais.model.Banco
import ifpb.edu.br.pdm.doemais.model.UsuarioDAO
import ifpb.edu.br.pdm.doemais.viewmodel.UsuarioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPrincipal(navController: NavController, email :String) {
    var bancosProximos by remember { mutableStateOf<List<Banco>>(emptyList()) }
    var outrosBancos by remember { mutableStateOf<List<Banco>>(emptyList()) }
    var cidadeUsuario by remember { mutableStateOf<String>("") }

    LaunchedEffect(Unit) {
        UsuarioDAO().getId(email) { usuarioId ->
            if (usuarioId != null) {
                UsuarioDAO().getUsuarioCidade(usuarioId) { cidade ->
                    cidadeUsuario = cidade ?: ""

                    BancoDAO().buscarPorCidade(cidadeUsuario) { bancosProximosList ->
                        bancosProximos = bancosProximosList

                        BancoDAO().listarTodos { todosBancos ->
                            outrosBancos = todosBancos.filterNot { it.cidade == cidadeUsuario }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Locais de doação", color = Color(0xFF8B0000)) }) },
        bottomBar = {
            BottomNavigationBar(navController = navController, email)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (bancosProximos.isNotEmpty()) {
                Text(
                    text = "Próximos a Você",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                LazyColumn {
                    items(bancosProximos) { banco ->
                        BancoCard(banco, email, navController)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (outrosBancos.isNotEmpty()) {
                Text(
                    text = "Outros",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                LazyColumn {
                    items(outrosBancos) { banco ->
                        BancoCard(banco, email, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, email: String) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color(0xFF8B0000)) },
            label = { Text("Home", color = Color(0xFF8B0000)) },
            selected = currentRoute == "principal/$email",
            onClick = { navController.navigate("principal/$email") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color(0xFF8B0000)) },
            label = { Text("Perfil", color = Color(0xFF8B0000)) },
            selected = currentRoute == "perfil/$email",
            onClick = { navController.navigate("perfil/$email") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Agendamentos", tint = Color(0xFF8B0000)) },
            label = { Text("Histórico", color = Color(0xFF8B0000)) },
            selected = currentRoute == "agendamentos/$email",
            onClick = { navController.navigate("agendamentos/$email") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Locais", tint = Color(0xFF8B0000)) },
            label = { Text("Locais", color = Color(0xFF8B0000)) },
            selected = currentRoute == "hospitais/$email",
            onClick = { navController.navigate("hospitais/$email") }
        )
    }
}

@Composable
fun BancoCard(banco: Banco, email: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = banco.nome,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Endereço: ${banco.endereco}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Cidade: ${banco.cidade}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("agendar/${banco.id}/$email")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
            ) {
                Text("Agendar horário", color = Color.White)
            }
        }
    }
}
