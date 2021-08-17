package com.daguiar.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.daguiar.minhasfinancas.exception.ErroAutenticacaoException;
import com.daguiar.minhasfinancas.exception.RegraNegocioException;
import com.daguiar.minhasfinancas.model.entity.Usuario;
import com.daguiar.minhasfinancas.model.repository.UsuarioRepository;
import com.daguiar.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		Usuario result = service.autenticar(email, senha);
		
		Assertions.assertThat(result).isNotNull();
		
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		RuntimeException exception = assertThrows(ErroAutenticacaoException.class,
	            ()->{
	            	service.autenticar("email@email.com", "senha");
	            });
		assertEquals("Usuário não encontrado para o e-mail informado.", exception.getMessage());
		
	}
	
	@Test
	public void deveSalvarUmUsuario() {
		
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		
		Usuario usuario = new Usuario();
		usuario.setId(1l);
		usuario.setNome("nome");
		usuario.setEmail("email@email.com");
		usuario.setSenha("senha");
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		
		String email = "email@email.com";
		
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		assertThrows(RegraNegocioException.class,
	            ()->{
	            	service.salvarUsuario(usuario);
	            });
		
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
	}
	
	@Test
	public void develancarErroQuandoSenhaNaoBater() {
		
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		RuntimeException exception = assertThrows(ErroAutenticacaoException.class,
	            ()->{
	            	service.autenticar(email, "senha2");
	            });
		
		assertEquals("Senha inválida.", exception.getMessage());
		
	}
	
	@Test
	public void deveValidarEmail() {
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		service.validarEmail("usuario@email.com");
		
	}

	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		assertThrows(RegraNegocioException.class,
	            ()->{
	            	service.validarEmail("usuario@email.com");
	            });
		
	}
}
