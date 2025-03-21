package ifpb.edu.br.pdm.doemais.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class UsuarioDAO {
    private val db = FirebaseFirestore.getInstance()
    private val usuariosRef = db.collection("usuarios")

    fun criar(usuario: Usuario, callback: (Boolean) -> Unit) {
        usuariosRef.document(usuario.id).set(usuario)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun buscarPorEmail(email: String, callback: (Usuario?) -> Unit) {
        usuariosRef.whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val usuario = doc.toObject(Usuario::class.java)?.copy(id = doc.id)
                    callback(usuario)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { callback(null) }
    }

    fun getId(email: String, callback: (String?) -> Unit) {
        usuariosRef.whereEqualTo("email", email).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    val usuarioId = document.id
                    callback(usuarioId)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun atualizar(id: String, usuario: Usuario, callback: (Boolean) -> Unit) {
        usuariosRef.document(id).set(usuario)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun deletar(id: String, callback: (Boolean) -> Unit) {
        usuariosRef.document(id).delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}