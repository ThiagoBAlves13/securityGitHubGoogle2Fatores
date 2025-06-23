package br.com.forum_hub.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.forum_hub.domain.autenticacao.github.LoginGithubService;

@RestController
@RequestMapping("/login/github")
public class LoginGithubController {
	
	@Autowired
	LoginGithubService loginGithubService;

	@GetMapping
	public ResponseEntity<?> redirecionarGithub(){
		
		String url = loginGithubService.gerarUrl();
		
		var headers = new HttpHeaders();
		headers.setLocation(URI.create(url));
		
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
	
	@GetMapping("/autorizado")
	public ResponseEntity<?> obterToker(@RequestParam String code){
		
		var token = loginGithubService.obterToken(code);
		return ResponseEntity.ok(token);
	}
}
