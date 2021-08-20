package com.daguiar.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.daguiar.minhasfinancas.model.entity.Lancamento;
import com.daguiar.minhasfinancas.model.enums.StatusLancamento;
import com.daguiar.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		
		Lancamento lancamento = criarLancamento();
		lancamento = repository.save(lancamento);
		
		Assertions.assertThat(lancamento.getId()).isNotNull();
		
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(lancamentoInexistente).isNull();
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		lancamento.setAno(2018);
		lancamento.setDescricao("Descrição teste");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Descrição teste");
		Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
		
	}
	
	@Test
	public void deveBuscarUmLancamentoPorID() {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();
		
	}
	
	private Lancamento criarEPersistirUmLancamento() {
		
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
		
	}
	
	public static Lancamento criarLancamento() {
		
		Lancamento lancamento = new Lancamento();
		lancamento.setAno(2021);
		lancamento.setMes(1);
		lancamento.setDescricao("Lançamento qualquer");
		lancamento.setValor(BigDecimal.valueOf(1000));
		lancamento.setTipo(TipoLancamento.RECEITA);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		lancamento.setDataCadastro(LocalDate.now());
		
		return lancamento;
		
	}

}
