package br.com.eazybytes.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.eazybytes.model.Authority;

public interface AuthorityRepository extends CrudRepository<Authority, Long>{
	
}
