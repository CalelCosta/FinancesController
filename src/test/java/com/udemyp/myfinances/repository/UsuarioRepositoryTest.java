package com.udemyp.myfinances.repository;

import com.udemyp.myfinances.model.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Test
    public void verificaUmEmail(){
        //Cenário
      Usuario u = Usuario.builder().nome("user").email("user@email.com").build();
      usuarioRepository.save(u);

      //Ação
        boolean result = usuarioRepository.existsByEmail("user@email.com");

        //Verificação
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void verificarUsusarioSemEmail(){
        //Cenário
        usuarioRepository.deleteAll();
        //Ação
        boolean result = usuarioRepository.existsByEmail("user@email.com");
        //Verificação
        Assertions.assertThat(result).isFalse();
    }
}
