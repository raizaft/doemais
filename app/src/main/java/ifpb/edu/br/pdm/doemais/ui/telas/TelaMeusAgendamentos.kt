package ifpb.edu.br.pdm.doemais.ui.telas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ifpb.edu.br.pdm.doemais.model.AgendamentoDAO
import ifpb.edu.br.pdm.doemais.model.Agendamento
import ifpb.edu.br.pdm.doemais.model.BancoDAO
import ifpb.edu.br.pdm.doemais.model.UsuarioDAO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaMeusAgendamentos(email: String, navController: NavController) {
    var agendamentos by remember { mutableStateOf<List<Agendamento>>(emptyList()) }

    LaunchedEffect(Unit) {
        UsuarioDAO().getId(email) { usuarioId ->
            if (usuarioId != null) {
                AgendamentoDAO().listarAgendamentos(usuarioId) { agendamentos = it }
            }
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Meus Agendamentos", color = Color(0xFF8B0000)) }) },
        bottomBar = {
            BottomNavigationBar(navController = navController, email)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(agendamentos) { agendamento ->
                    AgendamentoCard(agendamento, navController)
                }
            }
        }
    }

}

@Composable
fun AgendamentoCard(agendamento: Agendamento, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = agendamento.data,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = agendamento.horario,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = agendamento.status,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (agendamento.status == "Agendado") {
                val context = LocalContext.current
                Button(
                    onClick = {
                        val agendamentoDAO = AgendamentoDAO()
                        agendamentoDAO.cancelarAgendamento(agendamento) { success ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Agendamento cancelado com sucesso",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erro ao cancelar agendamento",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
                ) {
                    Text("Cancelar Agendamento", color = Color.White)
                }
            }

        }
    }
}

