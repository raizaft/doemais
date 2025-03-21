package ifpb.edu.br.pdm.doemais.model

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Usuario (
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
    val cep: String = "",
    val peso: Double = 0.0,
    val altura: Int = 0,
    val idade: Int = 0,
    val cidade: String = ""
) : Serializable