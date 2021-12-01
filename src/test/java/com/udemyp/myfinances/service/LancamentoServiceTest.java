package com.udemyp.myfinances.service;

import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.model.Lancamento;
import com.udemyp.myfinances.model.Usuario;
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
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void deveDeletarLancamento(){
        Lancamento lancamentoDeletar = LancamentoRepositoryTest.criaLancamento();
        lancamentoDeletar.setId(1L);

        lancamentoService.deletar(lancamentoDeletar);

        Mockito.verify(lancamentoRepository).delete(lancamentoDeletar);

    }

    @Test
    public void deveFiltrarLancamentos(){
        //Cenário
        Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
        lancamento.setId(1L);

        List<Lancamento> list = Arrays.asList(lancamento);
        Mockito.when(lancamentoRepository.findAll(Mockito.any(Example.class))).thenReturn(list);

        //Execução
        List<Lancamento> resultado = lancamentoService.buscar(lancamento);

        //Verificação
        Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
    }

    @Test
    public void deveAtualizarStatusELancamento(){
        //cenário
        Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(lancamentoService).atualizar(lancamento);

        //execução
        lancamentoService.atualizarStatus(lancamento, novoStatus);

        //verificação
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(lancamentoService).atualizar(lancamento);


    }

    @Test
    public void deveObterLancamentoPorId(){
        Long id = 1L; //criado pois precisamos da palavra ID no when do mockito

        Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
        lancamento.setId(id);

        Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = lancamentoService.obterPorId(id);

        Assertions.assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioSemId(){
        Long id = 1L; //criado pois precisamos da palavra ID no when do mockito

        Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
        lancamento.setId(id);

        Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = lancamentoService.obterPorId(id);

        Assertions.assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErroAoValidarLancamento(){
        Lancamento lancamento = new Lancamento();

        Throwable erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        lancamento.setDescricao("");

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        lancamento.setDescricao("Salario");

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(0);

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(13);

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(1);

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(203);

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(2021);

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário válido.");

        lancamento.setUsuario(new Usuario());

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário válido.");

        lancamento.getUsuario().setId(1L);

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor maior que 0");

        lancamento.setValor(BigDecimal.ZERO);

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor maior que 0");

        lancamento.setValor(BigDecimal.valueOf(1));

        erro = Assertions.catchThrowable(() ->lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lançamento.");
    }

}
