package ifpb.edu.br.pdm.doemais.model

import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Banco (
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val endereco: String = "",
    val cidade: String = "",
    val horarioAbertura: String = "",
    val horarioFechamento: String = "",
    val horariosDisponiveis: MutableList<String> = mutableListOf()
) {
    fun gerarHorarios() {
        horariosDisponiveis.clear()
        val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
        val abertura = formato.parse(horarioAbertura)
        val fechamento = formato.parse(horarioFechamento)
        val calendario = Calendar.getInstance()
        if (abertura != null) {
            calendario.time = abertura
        }
        while (calendario.time.before(fechamento)) {
            horariosDisponiveis.add(formato.format(calendario.time))
            calendario.add(Calendar.MINUTE, 30)
        }
    }
}