package com.daguiar.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daguiar.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
