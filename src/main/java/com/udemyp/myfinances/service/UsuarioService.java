package com.udemyp.myfinances.service;

import com.udemyp.myfinances.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email);
}
