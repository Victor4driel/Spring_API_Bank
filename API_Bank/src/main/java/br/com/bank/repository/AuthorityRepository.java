package br.com.bank.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.bank.model.Authority;

public interface AuthorityRepository extends CrudRepository<Authority, Long>{
	
}
