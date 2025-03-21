package ifpb.edu.br.pdm.doemais

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import ifpb.edu.br.pdm.doemais.ui.telas.TelaCadastro
import ifpb.edu.br.pdm.doemais.ui.telas.TelaAgendar
import ifpb.edu.br.pdm.doemais.ui.telas.TelaHospitais
import ifpb.edu.br.pdm.doemais.ui.telas.TelaLogin
import ifpb.edu.br.pdm.doemais.ui.telas.TelaMeusAgendamentos
import ifpb.edu.br.pdm.doemais.ui.telas.TelaPerfil
import ifpb.edu.br.pdm.doemais.ui.telas.TelaPrincipal
import ifpb.edu.br.pdm.doemais.ui.theme.DoemaisTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            DoemaisTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            TelaLogin(modifier = Modifier.padding(innerPadding), navController = navController, onSigninClick = {
                                navController.navigate("principal")
                            })
                        }
                        composable("cadastro") {
                            TelaCadastro(navController = navController)
                        }
                        composable("principal/{email}") { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            TelaPrincipal(navController = navController, email = email)
                        }
                        composable("perfil/{email}") { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            TelaPerfil(navController = navController, email = email)
                        }
                        composable("agendar/{bancoId}/{email}") { backStackEntry ->
                            val bancoId = backStackEntry.arguments?.getString("bancoId") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            TelaAgendar(bancoId = bancoId, email = email, navController = navController)
                        }
                        composable("agendamentos/{email}") { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            TelaMeusAgendamentos(email = email, navController = navController)
                        }
                        composable("hospitais/{email}") { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            TelaHospitais(email = email, navController = navController)
                        }
                    }
                }
            }
        }
    }
}