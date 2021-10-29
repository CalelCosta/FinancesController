package com.udemyp.myfinances.service;


import com.udemyp.myfinances.Exception.ErroAutenticacao;
import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.model.Usuario;
import com.udemyp.myfinances.repository.UsuarioRepository;
import com.udemyp.myfinances.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioServiceTest {

    UsuarioService usuarioService;

    @MockBean
    UsuarioRepository usuarioRepository;

    @Before
    public void setUp(){
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        usuarioService = new UsuarioServiceImpl(usuarioRepository);
    }

    @Test(expected = Test.None.class)
    public void validaEmail(){
        //Cenário
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //Ação
        usuarioService.validarEmail("user@email.com");
    }

    @Test(expected = RegraNegocioException.class)
    public void lançaErroComEmailCadastrado(){
        //Cenário
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //Ação
        usuarioService.validarEmail("user@email.com");
    }

    @Test(expected = Test.None.class)
    public void autenticarComSucesso(){
        String email = "email@email.com";
        String senha = "senha";

        Usuario u = Usuario.builder().email(email).senha(senha).id(1l).build();
        Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(u));

        Usuario result = usuarioService.autenticar(email, senha);

        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void erroAoNaoEncontrarEmailUsuario(){
        //Cenario
        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //Ação
        Throwable exception = Assertions.catchThrowable( () -> usuarioService.autenticar("email@email.com", "senha"));

        //Verificação
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado!");
    }

    @Test
    public void erroNaoEncontraSenhaUsuario(){
        //Cenario
        String email = "email@email.com";
        String senha = "senha";

        Usuario u = Usuario.builder().email(email).senha(senha).id(1l).build();

        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(u));

        //Ação
       Throwable exception = Assertions.catchThrowable( () -> usuarioService.autenticar(email,"123"));
       Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha Inválida!");
    }
}
