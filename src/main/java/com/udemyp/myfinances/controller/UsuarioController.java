package com.udemyp.myfinances.controller;

import com.udemyp.myfinances.Exception.ErroAutenticacao;
import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.dto.UsuarioDTO;
import com.udemyp.myfinances.model.Usuario;
import com.udemyp.myfinances.service.LancamentoService;
import com.udemyp.myfinances.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final LancamentoService lancamentoService;


    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable Long id){
        Optional<Usuario> usuario = usuarioService.obterPorId(id);
        if (!usuario.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return new ResponseEntity(saldo, HttpStatus.OK);
    }

    @PostMapping("/autenticar")
    public ResponseEntity autenticarUsuario(@RequestBody UsuarioDTO dto){
        try {
            Usuario usuarioAuth = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
            return new ResponseEntity(usuarioAuth, HttpStatus.OK);
        }catch (ErroAutenticacao err){
            return ResponseEntity.badRequest().body(err.getMessage());
        }
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
