package com.daguiar.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.daguiar.minhasfinancas.exception.RegraNegocioException;
import com.daguiar.minhasfinancas.model.entity.Lancamento;
import com.daguiar.minhasfinancas.model.entity.Usuario;
import com.daguiar.minhasfinancas.model.enums.StatusLancamento;
import com.daguiar.minhasfinancas.model.enums.TipoLancamento;
import com.daguiar.minhasfinancas.model.repository.LancamentoRepository;
import com.daguiar.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.daguiar.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		service.atualizar(lancamentoSalvo);
		
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), 
										RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), 
										NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
		
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		service.deletar(lancamento);
		
		Mockito.verify(repository).delete(lancamento);
		
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), 
										NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
		
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Lancamento> resultado = service.buscar(lancamento);
		
		Assertions
			.assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
		
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento statusNovo = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		service.atualizarStatus(lancamento, statusNovo);
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(statusNovo);
		Mockito.verify(service).atualizar(lancamento);
		
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> resultado = service.obterPorId(id);
		Assertions.assertThat(resultado.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> resultado = service.obterPorId(id);
		Assertions.assertThat(resultado.isPresent()).isFalse();
		
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		
		Lancamento lancamento = new Lancamento();
		RuntimeException exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe uma descrição válida.", exception.getMessage());
		
		lancamento.setDescricao(" ");
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe uma descrição válida.", exception.getMessage());
		
		lancamento.setDescricao("Descrição teste");
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um mês válido.", exception.getMessage());
		
		lancamento.setMes(0);
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um mês válido.", exception.getMessage());
		
		lancamento.setMes(13);
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um mês válido.", exception.getMessage());
		
		lancamento.setMes(1);
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um ano válido.", exception.getMessage());
		
		lancamento.setAno(202);
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um ano válido.", exception.getMessage());
		
		lancamento.setAno(20202);
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um ano válido.", exception.getMessage());
		
		lancamento.setAno(2021);
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um usuário.", exception.getMessage());
		
		
		Usuario usuario = new Usuario();
		lancamento.setUsuario(usuario);
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um usuário.", exception.getMessage());
		
		lancamento.getUsuario().setId(1l);
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um valor válido.", exception.getMessage());
		
		lancamento.setValor(BigDecimal.valueOf(0));
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um valor válido.", exception.getMessage());
		
		lancamento.setValor(BigDecimal.valueOf(1000));
		exception = assertThrows(RegraNegocioException.class, 
				() -> {
					service.validar(lancamento);
				});
		assertEquals("Informe um tipo de lançamento.", exception.getMessage());
		
		lancamento.setTipo(TipoLancamento.RECEITA);
		service.validar(lancamento);
		
	}

}
