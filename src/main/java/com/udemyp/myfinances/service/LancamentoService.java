package com.udemyp.myfinances.service;

import com.udemyp.myfinances.model.Lancamento;
import com.udemyp.myfinances.modelEnums.StatusLancamento;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface LancamentoService {
    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    void deletar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento lancamentoFiltro);

    void atualizarStatus(Lancamento lancamento, StatusLancamento status);

    void validar(Lancamento lancamento);

    Optional<Lancamento> obterPorId(Long id);
}
