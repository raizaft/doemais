package ifpb.edu.br.pdm.doemais.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AgendamentoDAO {
    private val db = FirebaseFirestore.getInstance()
    private val agendamentosRef = db.collection("agendamentos")

    fun salvarAgendamento(usuarioId: String, bancoId: String, data: String, horario: String, callback: (Boolean) -> Unit) {
        val agendamento = hashMapOf(
            "usuarioId" to usuarioId,
            "bancoId" to bancoId,
            "data" to data,
            "horario" to horario,
            "status" to "Agendado"
        )

        agendamentosRef.add(agendamento)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Erro ao salvar agendamento: ${exception.message}")
                callback(false)
            }
    }

    fun listarAgendamentos(usuarioId: String, callback: (List<Agendamento>) -> Unit) {
        agendamentosRef.whereEqualTo("usuarioId", usuarioId)
            .get()
            .addOnSuccessListener { result ->
                val agendamentos = result.documents.mapNotNull { doc ->
                    doc.toObject(Agendamento::class.java)
                }
                callback(agendamentos)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Erro ao listar agendamentos: ${exception.message}")
                callback(emptyList())
            }
    }

    fun cancelarAgendamento(agendamento: Agendamento, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val agendamentosRef = db.collection("agendamentos")

        agendamentosRef.whereEqualTo("usuarioId", agendamento.usuarioId)
            .whereEqualTo("data", agendamento.data)
            .whereEqualTo("horario", agendamento.horario)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentId = documents.documents[0].id

                    agendamentosRef.document(documentId)
                        .update("status", "Cancelado")
                        .addOnSuccessListener {
                            BancoDAO().restaurarHorario(agendamento.bancoId, agendamento.horario) { success ->
                                if (success) {
                                    callback(true)
                                } else {
                                    callback(false)
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("FirestoreError", "Erro ao cancelar agendamento: ${exception.message}")
                            callback(false)
                        }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Erro ao buscar agendamento para cancelamento: ${exception.message}")
                callback(false)
            }
    }



}
