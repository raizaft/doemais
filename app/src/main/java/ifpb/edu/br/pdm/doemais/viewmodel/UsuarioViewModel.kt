package ifpb.edu.br.pdm.doemais.viewmodel

import androidx.lifecycle.ViewModel
import ifpb.edu.br.pdm.doemais.model.Usuario

class UsuarioViewModel : ViewModel() {
    private var usuario: Usuario? = null

    fun setUsuario(user: Usuario) {
        usuario = user
    }

    fun getUsuario(): Usuario? {
        return usuario
    }
}
