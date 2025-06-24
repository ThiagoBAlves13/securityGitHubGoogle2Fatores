package br.com.forum_hub.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.forum_hub.domain.autenticacao.DadosToken;
import br.com.forum_hub.domain.autenticacao.TokenService;
import br.com.forum_hub.domain.autenticacao.github.LoginGithubService;
import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioService;

@RestController
@RequestMapping("/login/github")
public class LoginGithubController {
	
	@Autowired
	LoginGithubService loginGithubService;
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	TokenService tokenService;

	@GetMapping
	public ResponseEntity<?> redirecionarGithub(){
		
		String url = loginGithubService.gerarUrl();
		
		var headers = new HttpHeaders();
		headers.setLocation(URI.create(url));
		
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
	
	@GetMapping("/autorizado")
	public ResponseEntity<DadosToken> autenticarUsuarioOAuth(@RequestParam String code){
		
		var email = loginGithubService.obterEmail(code);
		
		Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
		
		var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return ResponseEntity.ok(tokenService.obterDadosToken((Usuario)authentication.getPrincipal()));
	}
}
