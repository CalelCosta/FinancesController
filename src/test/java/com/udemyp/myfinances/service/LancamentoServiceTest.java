package com.udemyp.myfinances.service;

import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.model.Lancamento;
import com.udemyp.myfinances.modelEnums.StatusLancamento;
import com.udemyp.myfinances.repository.LancamentoRepository;
import com.udemyp.myfinances.repository.LancamentoRepositoryTest;
import com.udemyp.myfinances.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl lancamentoService;

    @MockBean
    LancamentoRepository lancamentoRepository;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamentoSalvar = LancamentoRepositoryTest.criaLancamento(); //Dessa forma chamamos métodos publicos e estáticos de outra classe.
        Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvar);
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criaLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(lancamentoRepository.save(lancamentoSalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = lancamentoService.salvar(lancamentoSalvar);

        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarComErro(){
        Lancamento lancamentoSalvar = LancamentoRepositoryTest.criaLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamentoSalvar);

        Assertions.catchThrowableOfType(() ->lancamentoService.salvar(lancamentoSalvar), RegraNegocioException.class);

        Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoSalvar);
    }

    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criaLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvo);

        Mockito.when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        lancamentoService.atualizar(lancamentoSalvo);

        Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoAtualizarSemId(){
        Lancamento lancamentoSalvar = LancamentoRepositoryTest.criaLancamento();

        Assertions.catchThrowableOfType(() ->lancamentoService.atualizar(lancamentoSalvar), NullPointerException.class);

        Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoSalvar);
    }
}
