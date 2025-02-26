package ifpb.edu.br.pdm.doemais.ui.telas

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import ifpb.edu.br.pdm.doemais.model.Usuario

@Composable
fun TelaPrincipal(modifier: Modifier = Modifier, onLogoffClick: () -> Unit) {
    var scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Text(text = "Tela Principal")

        Button(onClick = { onLogoffClick() }) {
            Text("Sair")
        }
    }

}