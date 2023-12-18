package br.com.eazybytes.config;

import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import br.com.eazybytes.filter.AuthoritiesLoggingAfterFilter;
import br.com.eazybytes.filter.CsrfCookieFilter;
import br.com.eazybytes.filter.RequestValidationBeforeFilter;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class ProjectSecurityConfig {
	
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		requestHandler.setCsrfRequestAttributeName("_csrf");
		
		return http
					.securityContext(context -> context.requireExplicitSave(false))
					.sessionManagement(section -> section.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
					.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
						@Override
						public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
							CorsConfiguration config = new CorsConfiguration();
							config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
							config.setAllowedMethods(Collections.singletonList("*"));
							config.setAllowCredentials(true);
							config.setAllowedHeaders(Collections.singletonList("*"));
							config.setMaxAge(3600L);
							return config;
						}
					}))
					
					.authorizeHttpRequests(authorizeRequests -> authorizeRequests
								.requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
								.requestMatchers("/myBalance").hasAnyAuthority("VIEWACCOUNT", "VIEWBALANCE")
								.requestMatchers("/myLoans").hasAuthority("VIEWLOANS")
								.requestMatchers("/myCards").hasAuthority("VIEWCARDS")
								.requestMatchers("/user").authenticated()
								.requestMatchers("/notices", "/contact", "/register").permitAll()
					) 
					.formLogin(Customizer.withDefaults())
					.httpBasic(Customizer.withDefaults())
					.csrf(csrf -> csrf.csrfTokenRequestHandler(requestHandler)
								.ignoringRequestMatchers("/contact", "/register", "/notices")
								.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
					.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
					.addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
					.addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
					.build();		
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
