package ifpb.edu.br.pdm.doemais.ui.telas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ifpb.edu.br.pdm.doemais.model.Usuario
import ifpb.edu.br.pdm.doemais.model.UsuarioDAO
import ifpb.edu.br.pdm.doemais.viewmodel.UsuarioViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaPerfil(navController: NavController, email: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(email) {
        UsuarioDAO().buscarPorEmail(email) { usuarioEncontrado ->
            usuario = usuarioEncontrado
            carregando = false
        }
    }
    when {
        carregando -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        usuario == null -> {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Usuário não encontrado!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
        else -> {
            PerfilContent(usuario!!, scope, context, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilContent(usuarioInicial: Usuario, scope: CoroutineScope, context: android.content.Context, navController: NavController) {
    val usuarioDAO = UsuarioDAO()
    var editMode by remember { mutableStateOf(false) }
    var usuario by remember { mutableStateOf(usuarioInicial) }

    var nome by remember { mutableStateOf(usuario.nome) }
    var email by remember { mutableStateOf(usuario.email) }
    var senha by remember { mutableStateOf(usuario.senha) }
    var cep by remember { mutableStateOf(usuario.cep) }
    var idade by remember { mutableStateOf(usuario.idade.toString()) }
    var altura by remember { mutableStateOf(usuario.altura.toString()) }
    var peso by remember { mutableStateOf(usuario.peso.toString()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Perfil", color = Color(0xFF8B0000)) }) },
        bottomBar = {
            BottomNavigationBar(navController = navController, email)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (editMode) {
                        OutlinedTextField(
                            value = nome,
                            onValueChange = { nome = it },
                            label = { Text("Nome") })
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") })
                        OutlinedTextField(
                            value = senha,
                            onValueChange = { senha = it },
                            label = { Text("Senha") })
                        OutlinedTextField(
                            value = cep,
                            onValueChange = { cep = it },
                            label = { Text("CEP") })
                        OutlinedTextField(
                            value = idade,
                            onValueChange = { idade = it },
                            label = { Text("Idade") })
                        OutlinedTextField(
                            value = altura,
                            onValueChange = { altura = it },
                            label = { Text("Altura") })
                        OutlinedTextField(
                            value = peso,
                            onValueChange = { peso = it },
                            label = { Text("Peso") })
                    } else {
                        PerfilInfo(label = "Nome", value = nome)
                        PerfilInfo(label = "Email", value = email)
                        PerfilInfo(label = "CEP", value = cep)
                        PerfilInfo(label = "Idade", value = idade)
                        PerfilInfo(label = "Altura", value = altura)
                        PerfilInfo(label = "Peso", value = peso)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (editMode) {
                        val usuarioAtualizado = usuario.copy(
                            nome = nome, email = email, senha = senha, cep = cep,
                            idade = idade.toIntOrNull() ?: usuario.idade,
                            altura = altura.toIntOrNull() ?: usuario.altura,
                            peso = peso.toDoubleOrNull() ?: usuario.peso
                        )

                        scope.launch(Dispatchers.IO) {
                            usuarioDAO.atualizar(usuario.id, usuarioAtualizado) { sucesso ->
                                scope.launch(Dispatchers.Main) {
                                    if (sucesso) {
                                        usuario = usuarioAtualizado
                                        editMode = false
                                        Toast.makeText(
                                            context,
                                            "Dados atualizados!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Erro ao atualizar",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    } else {
                        editMode = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
            ) {
                Text(
                    if (editMode) "Salvar Alterações" else "Editar Informações",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Sair", color = Color.White)
            }
        }
    }
}

@Composable
fun PerfilInfo(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", fontWeight = FontWeight.Bold)
        Text(value)
    }
}