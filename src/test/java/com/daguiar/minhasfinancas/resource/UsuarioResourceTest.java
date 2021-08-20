package com.daguiar.minhasfinancas.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.daguiar.minhasfinancas.api.dto.UsuarioDTO;
import com.daguiar.minhasfinancas.api.resource.UsuarioResource;
import com.daguiar.minhasfinancas.exception.ErroAutenticacaoException;
import com.daguiar.minhasfinancas.exception.RegraNegocioException;
import com.daguiar.minhasfinancas.model.entity.Usuario;
import com.daguiar.minhasfinancas.service.LancamentoService;
import com.daguiar.minhasfinancas.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {
	
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = new UsuarioDTO();
		dto.setEmail(email);
		dto.setSenha(senha);
		
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = new UsuarioDTO();
		dto.setEmail(email);
		dto.setSenha(senha);
		
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = new UsuarioDTO();
		dto.setEmail(email);
		dto.setSenha(senha);
		
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API)
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
		
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = new UsuarioDTO();
		dto.setEmail(email);
		dto.setSenha(senha);
		
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API)
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}

}
