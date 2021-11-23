package com.udemyp.myfinances.service.impl;

import com.udemyp.myfinances.Exception.ErroAutenticacao;
import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.model.Usuario;
import com.udemyp.myfinances.repository.UsuarioRepository;
import com.udemyp.myfinances.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super();
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario =  usuarioRepository.findByEmail(email);
        if (!usuario.isPresent()){
            throw new ErroAutenticacao("Usuário não encontrado para o email informado!");
        }
        if(!usuario.get().getSenha().equals(senha)){
            throw new ErroAutenticacao("Senha Inválida!");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
       boolean existe = usuarioRepository.existsByEmail(email);
       if (existe){
           throw new RegraNegocioException("Já existe um usuário com esse email cadastrado!");
       }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}
