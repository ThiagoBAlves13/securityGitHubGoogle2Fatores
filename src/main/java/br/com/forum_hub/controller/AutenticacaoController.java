package br.com.forum_hub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.forum_hub.domain.autenticacao.DadosLogin;
import br.com.forum_hub.domain.autenticacao.DadosRefreshToken;
import br.com.forum_hub.domain.autenticacao.DadosToken;
import br.com.forum_hub.domain.autenticacao.TokenService;
import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioService;
import jakarta.validation.Valid;


@RestController
public class AutenticacaoController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosLogin dados) {

        var autenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        Usuario usuario = (Usuario)authenticationManager.authenticate(autenticationToken).getPrincipal();

        return ResponseEntity.ok(tokenService.obterDadosToken(usuario));

    }

    @PostMapping("/atualizar-token")
    public ResponseEntity<?> atualizarToken(@RequestBody @Valid DadosRefreshToken dados) {

        var refreshToken = dados.refreshToken();
        var usuario = usuarioService.buscarUsuarioPorId(Long.valueOf(tokenService.verificarToken(refreshToken)));
        
        return ResponseEntity.ok(tokenService.obterDadosToken(usuario));

    }

}
