package ifpb.edu.br.pdm.doemais.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class BancoDAO {
    val db = FirebaseFirestore.getInstance()

    fun listarTodos(callback: (List<Banco>) -> Unit) {
        db.collection("bancos").get()
            .addOnSuccessListener { document ->
                val bancos = document.toObjects<Banco>()
                callback(bancos)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarPorNome(nome: String, callback: (Banco?) -> Unit) {
        db.collection("bancos").whereEqualTo("nome", nome).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val banco = document.documents[0].toObject<Banco>()
                    callback(banco)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun buscarPorId(id: String, callback: (Banco?) -> Unit) {
        db.collection("bancos").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val banco = document.toObject<Banco>()
                    callback(banco)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun criar(banco: Banco, callback: (Boolean) -> Unit) {
        val bancoRef = db.collection("bancos").document()
        val bancoComId = banco.copy(id =bancoRef.id)
        bancoRef.set(bancoComId)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun atualizar(id: String, novosDados: Banco, callback: (Boolean) -> Unit) {
        db.collection("bancos").document(id).set(novosDados)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun deletar(id: String, callback: (Boolean) -> Unit) {
        db.collection("bancos").document(id).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}