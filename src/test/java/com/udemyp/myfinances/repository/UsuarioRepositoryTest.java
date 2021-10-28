package com.udemyp.myfinances.repository;

import com.udemyp.myfinances.model.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void verificaUmEmail(){
        //Cenário
      Usuario u = criarUsuario();
      entityManager.persist(u);

      //Ação
        boolean result = usuarioRepository.existsByEmail("user@email.com");

        //Verificação
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void verificarUsusarioSemEmail(){
        //Ação
        boolean result = usuarioRepository.existsByEmail("user@email.com");
        //Verificação
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void persisteUsuarioNoDB(){
        //Cenário
        Usuario u = criarUsuario();
        //Ação
        Usuario uSalvo = usuarioRepository.save(u);
        //Verificação
        Assertions.assertThat(uSalvo.getId()).isNotNull();
    }

    @Test
    public void buscaUsuarioPorEmail(){
        Usuario u = criarUsuario();
        entityManager.persist(u);

        Optional<Usuario> result = usuarioRepository.findByEmail("user@email.com");

        Assertions.assertThat(result.isPresent()).isTrue();
    }

    public static Usuario criarUsuario(){
        return Usuario
                .builder()
                .nome("user")
                .email("user@email.com")
                .senha("senha")
                .build()
                ;
    }

    @Test
    public void retornaVazioSemEmail(){
        Usuario u = criarUsuario();
        entityManager.persist(u);

        Optional<Usuario> result = usuarioRepository.findByEmail("");

        Assertions.assertThat(result.isPresent()).isFalse();
    }

}
