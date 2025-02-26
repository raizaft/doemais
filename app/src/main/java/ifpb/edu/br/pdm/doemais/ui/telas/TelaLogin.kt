package ifpb.edu.br.pdm.doemais.ui.telas

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ifpb.edu.br.pdm.doemais.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ifpb.edu.br.pdm.doemais.model.UsuarioDAO

val usuarioDAO = UsuarioDAO()

@Composable
fun TelaLogin(modifier: Modifier = Modifier, navController: NavController, onSigninClick: (Usuario) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DOE+",
            color = Color(0xFF8B0000),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Login",
            color = Color(0xFF8B0000),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Insira seu email e sua senha",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Esqueceu a senha?",
            color = Color.DarkGray,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* Ação para recuperar senha */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    usuarioDAO.buscarPorEmail(email) { usuario ->
                        if (usuario != null && usuario.senha == senha) {
                            scope.launch(Dispatchers.Main) {
                                navController.navigate("principal/$email")
                            }
                        } else {
                            mensagemErro = "Email ou senha inválidos!"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Ainda não tem cadastro?")

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate("cadastro") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF8B0000))
        ) {
            Text("Criar conta", color = Color(0xFF8B0000))
        }
    }

    mensagemErro?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            mensagemErro = null
        }
    }
}