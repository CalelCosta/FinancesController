package com.udemyp.myfinances.service;


import com.udemyp.myfinances.Exception.RegraNegocioException;
import com.udemyp.myfinances.model.Usuario;
import com.udemyp.myfinances.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Test(expected = Test.None.class)
    public void validaEmail(){
        //Cenário
        usuarioRepository.deleteAll();
        //Ação
        usuarioService.validarEmail("user@email.com");
    }

    @Test
    public void lançaErroComEmailCadastrado(){
        //Cenário
        Usuario u = Usuario.builder().nome("u").email("servicos@email.com").build();
        usuarioRepository.save(u);

        //Ação
        usuarioService.validarEmail("user@email.com");
    }
}
