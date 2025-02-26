package ifpb.edu.br.pdm.doemais.model

import com.google.firebase.firestore.DocumentId

data class Banco (
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val endereco: String = "",
    val horario: String = ""
)