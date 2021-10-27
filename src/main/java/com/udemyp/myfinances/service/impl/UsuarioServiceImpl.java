package com.udemyp.myfinances.service.impl;

import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.model.Usuario;
import com.udemyp.myfinances.repository.UsuarioRepository;
import com.udemyp.myfinances.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario autenticar(String email, String senha) {
        return null;
    }

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        return null;
    }

    @Override
    public void validarEmail(String email) {
       boolean existe = usuarioRepository.existsByEmail(email);
       if (existe){
           throw new RegraNegocioException("Já existe um usuário com esse email cadastrado!");
       }
    }
}
