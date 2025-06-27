package br.com.forum_hub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioService;

@RestController
public class A2fController {

	@Autowired
	UsuarioService usuarioService;

	@PatchMapping("configurar-a2f")
	public ResponseEntity<?> gerarQrCode(@AuthenticationPrincipal Usuario logado) {
		var url = usuarioService.gerarQrCode(logado);

		return ResponseEntity.ok(url);
	}

	public ResponseEntity<?> ativarA2f(@RequestParam String code
			, @AuthenticationPrincipal Usuario logado) {

		usuarioService.ativarA2f(code, logado);
		
		return ResponseEntity.noContent().build();
	}

}
