package com.udemyp.myfinances.repository;

import com.udemyp.myfinances.model.Lancamento;
import com.udemyp.myfinances.modelEnums.StatusLancamento;
import com.udemyp.myfinances.modelEnums.TipoLancamento;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository lancamentoRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void salvarLancamento(){
        Lancamento lancamento = criaLancamento();
        lancamento =  lancamentoRepository.save(lancamento);

        assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void deletarLancamento(){
        Lancamento lancamento = criaLancamento();
        entityManager.persist(lancamento);

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        lancamentoRepository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

        assertThat(lancamentoInexistente).isNull();

    }

    @Test
    public void deveAtualizarLancamento(){
        Lancamento lancamento = criarLancamentoEPersistir();

        lancamento.setAno(2018);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        lancamentoRepository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
        assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
        assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
    }

    @Test
    public void deveBuscarlancamentoPorId(){
        Lancamento lancamento = criarLancamentoEPersistir();

        Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(lancamento.getId());

        assertThat(lancamentoEncontrado).isPresent();
    }

    private Lancamento criarLancamentoEPersistir(){
        Lancamento lancamento = criaLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }

    private Lancamento criaLancamento(){
        return Lancamento.builder()
                .ano(2021)
                .mes(1)
                .descricao("lançamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }
}
