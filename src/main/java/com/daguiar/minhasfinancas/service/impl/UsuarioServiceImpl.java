package com.daguiar.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daguiar.minhasfinancas.exception.ErroAutenticacaoException;
import com.daguiar.minhasfinancas.exception.RegraNegocioException;
import com.daguiar.minhasfinancas.model.entity.Usuario;
import com.daguiar.minhasfinancas.model.repository.UsuarioRepository;
import com.daguiar.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private UsuarioRepository repository;

	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		if(!usuario.isPresent()) {
			throw new ErroAutenticacaoException("Usuário não encontrado para o e-mail informado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacaoException("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		
		boolean existeEmail = repository.existsByEmail(email);
		if(existeEmail) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
		}
		
	}

}
