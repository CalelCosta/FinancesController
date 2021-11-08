package com.udemyp.myfinances.controller;

import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.dto.UsuarioDTO;
import com.udemyp.myfinances.model.Usuario;
import com.udemyp.myfinances.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity salvarUsuario(@RequestBody UsuarioDTO dto ){
        Usuario us = Usuario
                .builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build()
                ;
        try {
            Usuario usuarioSalvo = usuarioService.salvarUsuario(us);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
