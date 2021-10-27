package com.udemyp.myfinances.repository;

import com.udemyp.myfinances.model.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
}
