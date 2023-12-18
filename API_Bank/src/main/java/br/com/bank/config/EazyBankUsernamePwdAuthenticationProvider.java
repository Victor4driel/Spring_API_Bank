package br.com.bank.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.bank.model.Authority;
import br.com.bank.model.Customer;
import br.com.bank.repository.CustomerRepository;

@Component
public class EazyBankUsernamePwdAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {		
		String username = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		List<Customer> customer = customerRepository.findByEmail(username);
		if(customer.size() > 0) {
			if(passwordEncoder.matches(pwd, customer.get(0).getPwd())) {
				return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthority(customer.get(0).getAuthorities()));
			} else {
				throw new BadCredentialsException("Invalid password!");
			}
		} else {
			throw new BadCredentialsException("No user register with this details");
		}
		
	}
	
	private List<GrantedAuthority> getGrantedAuthority(Set<Authority> authorities) {
		List<GrantedAuthority> grantedAuthority = new ArrayList<>();
		for(Authority authority: authorities) {
			grantedAuthority.add(new SimpleGrantedAuthority(authority.getName()));
		}
		return grantedAuthority;
	}
	
	
	
	// É pra conseguir usarmos o usuario e senha como autenticação
	// O tipo de autenticação vamos trabalhar
	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
