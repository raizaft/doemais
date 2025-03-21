package ifpb.edu.br.pdm.doemais.ui.telas

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ifpb.edu.br.pdm.doemais.model.AgendamentoDAO
import ifpb.edu.br.pdm.doemais.model.BancoDAO
import ifpb.edu.br.pdm.doemais.model.UsuarioDAO
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAgendar(bancoId: String, email: String, navController: NavController) {
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var availableTimes by remember { mutableStateOf<List<String>>(emptyList()) }
    var isAgendamentoConcluido by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            navController.context,
            { _, year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
                BancoDAO().getHorariosDisponiveis(bancoId, selectedDate) { times ->
                    availableTimes = times
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { showDatePickerDialog() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = if (selectedDate.isEmpty()) "Escolha a Data" else "Data Selecionada: $selectedDate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (availableTimes.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Escolha o Horário")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = { expanded = !expanded }
                        )
                        .background(Color.Gray.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (selectedTime.isEmpty()) "Selecione o horário" else "Horário Selecionado: $selectedTime",
                        color = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availableTimes.forEach { time ->
                        DropdownMenuItem(
                            onClick = {
                                selectedTime = time
                                expanded = false
                            },
                            text = {
                                Text(text = time)
                            },
                            modifier = Modifier.padding(8.dp),
                            enabled = true,
                            colors = MenuDefaults.itemColors(
                                disabledTextColor = Color.Gray,
                                textColor = Color.Black
                            ),
                            contentPadding = PaddingValues(16.dp)
                        )
                    }
                }
            }
        } else {
            Text(text = "Nenhum horário disponível para a data selecionada.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedTime.isNotEmpty() && selectedDate.isNotEmpty()) {
                    UsuarioDAO().getId(email) { usuarioId ->
                        if (usuarioId != null) {
                            Log.d("Usuario", "Usuário encontrado com ID: $usuarioId")

                            BancoDAO().reservarHorario(bancoId, selectedTime) { success ->
                                if (success) {
                                    val agendamentoDAO = AgendamentoDAO()
                                    agendamentoDAO.salvarAgendamento(usuarioId, bancoId, selectedDate, selectedTime) { saved ->
                                        if (saved) {
                                            isAgendamentoConcluido = true
                                            Toast.makeText(navController.context, "Agendamento confirmado e salvo!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("principal/$email")
                                        } else {
                                            Toast.makeText(navController.context, "Erro ao salvar o agendamento.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(navController.context, "Erro ao reservar o horário.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.d("Usuario", "Usuário não encontrado com o email: $email")
                            Toast.makeText(navController.context, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedTime.isNotEmpty() && selectedDate.isNotEmpty()
        ) {
            Text("Confirmar Agendamento")
        }

        if (isAgendamentoConcluido) {
            Text(
                text = "Agendamento realizado com sucesso!",
                color = Color.Green,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

