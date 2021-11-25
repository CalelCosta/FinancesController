package com.udemyp.myfinances.controller;

import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.dto.AtualizaStatusDTO;
import com.udemyp.myfinances.dto.LancamentoDTO;
import com.udemyp.myfinances.model.Lancamento;
import com.udemyp.myfinances.model.Usuario;
import com.udemyp.myfinances.modelEnums.StatusLancamento;
import com.udemyp.myfinances.modelEnums.TipoLancamento;
import com.udemyp.myfinances.service.LancamentoService;
import com.udemyp.myfinances.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class lancamentoController {

    private final LancamentoService lancamentoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long usuario
            ){
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> u = usuarioService.obterPorId(usuario);
        if (!u.isPresent()){
            return ResponseEntity.badRequest()
                    .body("Não foi possível realizar a consulta. Usuário não encontrado para o ID informado");
        }else{
            lancamentoFiltro.setUsuario(u.get());
        }
        List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
        return new ResponseEntity(lancamentos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto){
        try {
            Lancamento entidade = converter(dto);
            entidade = lancamentoService.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Long id, @RequestBody LancamentoDTO dto){
        return lancamentoService.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                lancamentoService.atualizar(lancamento);
                return new ResponseEntity(lancamento, HttpStatus.OK);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }).orElseGet(() ->ResponseEntity.badRequest()
                    .body("Lançamento não encontrado na base de dados!"));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusDTO dto){
        return lancamentoService.obterPorId(id).map(entity -> {
                StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
                if (statusSelecionado == null){
                    return ResponseEntity.badRequest()
                            .body("Não foi possível atualizar o status do lançamento. Envie um status válido.");
                }
                try {
                    entity.setStatus(statusSelecionado);
                    lancamentoService.atualizar(entity);
                    return new ResponseEntity(entity, HttpStatus.OK);
                }catch (RegraNegocioException e){
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
        }).orElseGet(() ->ResponseEntity.badRequest()
                .body("Lançamento não encontrado na base de dados!"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable Long id){
        return lancamentoService.obterPorId(id).map(entity ->{
            try {
                lancamentoService.deletar(entity);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->ResponseEntity.badRequest()
                .body("Lançamento não encontrado na base de dados!"));
    }

    //Construtor padrão de conversão
    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();

        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).
                orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID informado."));

        lancamento.setUsuario(usuario);

        if (dto.getTipo() != null){
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        if (dto.getStatus() != null){
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }
}
