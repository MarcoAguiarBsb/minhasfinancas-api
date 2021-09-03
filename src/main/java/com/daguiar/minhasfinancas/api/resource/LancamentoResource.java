package com.daguiar.minhasfinancas.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daguiar.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.daguiar.minhasfinancas.api.dto.LancamentoDTO;
import com.daguiar.minhasfinancas.exception.RegraNegocioException;
import com.daguiar.minhasfinancas.model.entity.Lancamento;
import com.daguiar.minhasfinancas.model.entity.Usuario;
import com.daguiar.minhasfinancas.model.enums.StatusLancamento;
import com.daguiar.minhasfinancas.model.enums.TipoLancamento;
import com.daguiar.minhasfinancas.service.LancamentoService;
import com.daguiar.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {
	
	private LancamentoService service;
	
	private UsuarioService usuarioService;
	
	public LancamentoResource(LancamentoService service, UsuarioService usuarioService) {
		this.service = service;
		this.usuarioService = usuarioService;
	}
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario
			) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		Optional<Usuario> usuario = usuarioService.obterPorID(idUsuario);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado.");
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@GetMapping("{id}")
	public ResponseEntity obterLancamento(@PathVariable("id") Long id) {
		return service.obterPorId(id)
				.map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		
		try {
			
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
			
		} catch(RegraNegocioException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
		
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		return service.obterPorId(id).map(entidade -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entidade.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch(RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> 
			new ResponseEntity("Lançamento não encontrado.", HttpStatus.BAD_REQUEST));
		
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		
		return service.obterPorId(id).map(entidade -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			if(statusSelecionado == null) {
				ResponseEntity.badRequest().body("Envie um status válido.");
			}
			
			try {
				
				entidade.setStatus(statusSelecionado);
				Lancamento lancamento = service.atualizar(entidade);
				return ResponseEntity.ok(lancamento);	
				
			} catch(RegraNegocioException e) {
				
				return ResponseEntity.badRequest().body(e.getMessage());
				
			}
			
		}).orElseGet( () -> 
		new ResponseEntity("Lançamento não encontrado.", HttpStatus.BAD_REQUEST));
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		
		return service.obterPorId(id).map(entidade -> {
			service.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet( () ->
			new ResponseEntity("Lançamento não encontrado.", HttpStatus.BAD_REQUEST));
		
	}
	
	private LancamentoDTO converter(Lancamento lancamento) {
		
		LancamentoDTO dto = new LancamentoDTO();
		dto.setId(lancamento.getId());
		dto.setDescricao(lancamento.getDescricao());
		dto.setValor(lancamento.getValor());
		dto.setMes(lancamento.getMes());
		dto.setAno(lancamento.getAno());
		dto.setStatus(lancamento.getStatus().name());
		dto.setTipo(lancamento.getTipo().name());
		dto.setUsuario(lancamento.getUsuario().getId());
		
		return dto;
		
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		Usuario usuario = usuarioService.obterPorID(dto.getUsuario())
		.orElseThrow( ()-> new RegraNegocioException("Usuário não encontrado."));
		lancamento.setUsuario(usuario);
		
		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));			
		}
		
		if(dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));			
		}
		
		return lancamento;
		
	}

}
