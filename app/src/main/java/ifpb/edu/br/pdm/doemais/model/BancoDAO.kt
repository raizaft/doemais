package ifpb.edu.br.pdm.doemais.model

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.*
import java.util.Calendar

class BancoDAO {
    private val db = FirebaseFirestore.getInstance()
    private val bancosRef = db.collection("bancos")

    fun salvar(banco: Banco, callback: (Boolean) -> Unit) {
        val horarios = gerarHorarios(banco.horarioAbertura, banco.horarioFechamento)
        val bancoComHorarios = banco.copy(horariosDisponiveis = horarios as MutableList<String>)

        bancosRef.document(banco.id).set(bancoComHorarios)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun listarTodos(callback: (List<Banco>) -> Unit) {
        bancosRef.get()
            .addOnSuccessListener { result ->
                val bancos = result.documents.mapNotNull { doc ->
                    doc.toObject(Banco::class.java)?.copy(id = doc.id)
                }
                callback(bancos)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    fun buscarPorCidade(cidade: String, callback: (List<Banco>) -> Unit) {
        bancosRef.whereEqualTo("cidade", cidade).get()
            .addOnSuccessListener { result ->
                val bancos = result.documents.mapNotNull { doc ->
                    doc.toObject(Banco::class.java)?.copy(id = doc.id)
                }
                callback(bancos)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    fun reservarHorario(bancoId: String, horario: String, callback: (Boolean) -> Unit) {
        val bancoRef = bancosRef.document(bancoId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(bancoRef)
            val banco = snapshot.toObject(Banco::class.java)

            banco?.let {
                if (it.horariosDisponiveis.contains(horario)) {
                    val novosHorarios = it.horariosDisponiveis.toMutableList()
                    novosHorarios.remove(horario)

                    transaction.update(bancoRef, "horariosDisponiveis", novosHorarios)
                    return@runTransaction true
                }
            }
            return@runTransaction false
        }
            .addOnSuccessListener { callback(it) }
            .addOnFailureListener { callback(false) }
    }

    fun restaurarHorario(bancoId: String, horario: String, callback: (Boolean) -> Unit) {
        val bancoRef = db.collection("bancos").document(bancoId)

        bancoRef.get().addOnSuccessListener { document ->
            val banco = document.toObject(Banco::class.java)
            banco?.let {
                val horariosDisponiveis = it.horariosDisponiveis.toMutableList()
                if (!horariosDisponiveis.contains(horario)) {
                    horariosDisponiveis.add(horario)
                    bancoRef.update("horariosDisponiveis", horariosDisponiveis)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("FirestoreError", "Erro ao restaurar horário: ${exception.message}")
                            callback(false)
                        }
                } else {
                    callback(true)
                }
            } ?: run {
                callback(false)
            }
        }.addOnFailureListener { exception ->
            Log.e("FirestoreError", "Erro ao buscar banco: ${exception.message}")
            callback(false)
        }
    }

    fun getHorariosDisponiveis(bancoId: String, data: String, callback: (List<String>) -> Unit) {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataSelecionada: Date = formato.parse(data) ?: return callback(emptyList())

        val calendar = Calendar.getInstance().apply {
            time = dataSelecionada
        }

        val diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK)
        if (diaDaSemana == Calendar.SATURDAY || diaDaSemana == Calendar.SUNDAY) {
            callback(emptyList())
            return
        }

        bancosRef.document(bancoId).get()
            .addOnSuccessListener { document ->
                val banco = document.toObject(Banco::class.java)
                banco?.let {
                    val horariosDisponiveis = it.horariosDisponiveis
                    callback(horariosDisponiveis)
                } ?: run {
                    callback(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Erro ao buscar horários: ${exception.message}")
                callback(emptyList())
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun gerarHorarios(abertura: String, fechamento: String): List<String> {
        val horarios = mutableListOf<String>()
        val formato = java.text.SimpleDateFormat("HH:mm")
        val aberturaTime = formato.parse(abertura)
        val fechamentoTime = formato.parse(fechamento)
        val calendario = java.util.Calendar.getInstance()

        if (aberturaTime != null) {
            calendario.time = aberturaTime
        }
        while (calendario.time.before(fechamentoTime) || calendario.time == fechamentoTime) {
            horarios.add(formato.format(calendario.time))
            calendario.add(java.util.Calendar.MINUTE, 30)
        }

        return horarios
    }
}