package ifpb.edu.br.pdm.doemais.ui.telas

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ifpb.edu.br.pdm.doemais.api.Element
import ifpb.edu.br.pdm.doemais.api.OverpassRetrofit
import ifpb.edu.br.pdm.doemais.api.RetrofitInstance
import ifpb.edu.br.pdm.doemais.model.UsuarioDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHospitais(navController: NavController, email: String) {
    var hospitais by remember { mutableStateOf<List<Element>>(emptyList()) }
    var cidade by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(email) {
        UsuarioDAO().buscarPorEmail(email) { usuario ->
            usuario?.cidade?.let {
                cidade = it
                scope.launch {
                    hospitais = buscarHospitaisNaCidade(it)
                }
            } ?: Log.e("TelaHospitais", "Usuário não encontrado")
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Unidades em ${cidade ?: "..." }") }) },
        bottomBar = {
            BottomNavigationBar(navController = navController, email)
        }
    ) {
        LazyColumn {
            items(hospitais) { hospital ->
                if (hospital.tags["name"] != null) {
                    Card(
                        modifier = androidx.compose.ui.Modifier.padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Text(
                            text = hospital.tags["name"] ?: "Nome desconhecido.",
                            modifier = androidx.compose.ui.Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

suspend fun buscarHospitaisNaCidade(cidade: String): List<Element> {
    return withContext(Dispatchers.IO) {
        try {
            val coordenadas = RetrofitInstance.api.getCoordenadas(cidade).firstOrNull()

            if (coordenadas != null) {
                val lat = coordenadas.lat
                val lon = coordenadas.lon

                val query = """[out:json];node["amenity"="hospital"](around:20000,$lat,$lon);out;"""
                val resposta = OverpassRetrofit.api.getHospitais(query)

                return@withContext resposta.elements
            } else {
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
